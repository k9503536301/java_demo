package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.TransactionProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;
    private final TransactionProducer transactionProducer;

    @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
            transactionRepository.saveAll(transactions);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }

    }

    public List<Transaction> parseJson() throws IOException {
        TransactionDto[] transactions;

        try(InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("mock_data/transactions.json")
        ){
            transactions = objectMapper.readValue(in, TransactionDto[].class);
        } catch(Exception e){
            throw new IOException(e);
        }

        return Arrays.stream(transactions)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Transaction toEntity(TransactionDto dto) {
        Account account = accountService.getAccountById(dto.getAccountId());

        return Transaction.builder()
                .transactionId(dto.getTransactionId())
                .account(account)
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .status(dto.getStatus())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .transactionId(entity.getTransactionId())
                .accountId(entity.getAccount().getAccountId())
                .amount(entity.getAmount())
                .transactionTime(entity.getTransactionTime())
                .status(entity.getStatus())
                .timestamp(entity.getTimestamp())
                .build();
    }

    @LogDataSourceError
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @LogDataSourceError
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @LogDataSourceError
    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @LogDataSourceError
    public void deleteTransactionById(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    @Transactional
    public Transaction acceptTransaction(TransactionDto transactionDto) {
        Transaction transaction = this.toEntity(transactionDto);
        Account account = transaction.getAccount();
        if (!account.getStatus().equals(AccountStatus.OPEN)) {
            return this.createTransaction(transaction);
        }

        transaction.setStatus(TransactionStatus.REQUESTED);

        BigDecimal requestingBalance = account.getBalance().add(transaction.getAmount());

        TransactionAcceptanceDto transactionToAcceptance = TransactionAcceptanceDto.builder()
                .accountId(account.getAccountId())
                .transactionId(transaction.getTransactionId())
                .clientId(account.getClient().getClientId())
                .transactionAmount(transaction.getAmount())
                .accountBalance(requestingBalance)
                .timestamp(transaction.getTimestamp())
                .build();

        transactionProducer.sendTransactionToAccept(transactionToAcceptance);

        account.setBalance(requestingBalance);
        accountService.updateAccount(account);

        return this.createTransaction(transaction);
    }

    @Transactional
    public void processTransaction(TransactionResultDto transactionResultDto) throws Exception {
        switch (transactionResultDto.getStatus()){
            case ACCEPTED -> acceptTransaction(transactionResultDto);
            case REJECTED -> rejectTransaction(transactionResultDto);
            case BLOCKED -> blockTransactionAndAccount(transactionResultDto);
            default -> throw new Exception("Transaction status is not valid");
        }

    }

    private void acceptTransaction(TransactionResultDto transactionResultDto) {
        Transaction transaction =  this.getTransactionById(transactionResultDto.getTransactionId());

        transaction.setStatus(TransactionStatus.ACCEPTED);
        updateTransaction(transaction);
    }

    private void rejectTransaction(TransactionResultDto transactionResultDto) {
        Transaction transaction =  this.getTransactionById(transactionResultDto.getTransactionId());

        transaction.setStatus(TransactionStatus.REJECTED);
        updateTransaction(transaction);

        Account account = transaction.getAccount();
        BigDecimal balance = account.getBalance().subtract(transaction.getAmount());
        account.setBalance(balance);
        accountService.updateAccount(account);
    }

    private void blockTransactionAndAccount(TransactionResultDto transactionResultDto) {
        Transaction transaction =  this.getTransactionById(transactionResultDto.getTransactionId());

        transaction.setStatus(TransactionStatus.BLOCKED);
        updateTransaction(transaction);

        Account account = transaction.getAccount();
        BigDecimal balance = account.getBalance().subtract(transaction.getAmount());
        BigDecimal frozenAmount = account.getFrozenAmount().add(transaction.getAmount());

        account.setBalance(balance);
        account.setFrozenAmount(frozenAmount);
        accountService.updateAccount(account);
    }
}
