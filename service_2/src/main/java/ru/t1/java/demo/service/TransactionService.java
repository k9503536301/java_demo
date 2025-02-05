package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.TransactionAcceptanceProducer;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
//    private final AccountService accountService;
    private final TransactionAcceptanceProducer transactionAcceptanceProducer;

//    public Transaction toEntity(TransactionDto dto) {
//        Account account = accountService.getAccountById(dto.getAccountId())
//                .orElseThrow(() -> new EntityNotFoundException("Transaction Account not found"));
//
//        return Transaction.builder()
//                .transactionId(dto.getTransactionId())
//                .account(account)
//                .amount(dto.getAmount())
//                .transactionTime(dto.getTransactionTime())
//                .status(dto.getStatus())
//                .timestamp(dto.getTimestamp())
//                .build();
//    }

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

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransactionById(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }

//    @Transactional
//    public Transaction acceptTransaction(TransactionAcceptanceDto transactionDto) {
//        Transaction transaction = this.toEntity(transactionDto);
//        Account account = transaction.getAccount();
//        if (!account.getStatus().equals(AccountStatus.OPEN)) {
//            return this.createTransaction(transaction);
//        }
//
//        transaction.setStatus(TransactionStatus.REQUESTED);
//
//        BigDecimal requestingBalance = account.getBalance().add(transaction.getAmount());
//
//        TransactionAcceptanceDto transactionToAcceptance = TransactionAcceptanceDto.builder()
//                .accountId(account.getAccountId())
//                .transactionId(transaction.getTransactionId())
//                .clientId(account.getClient().getClientId())
//                .transactionAmount(transaction.getAmount())
//                .accountBalance(requestingBalance)
//                .timestamp(transaction.getTimestamp())
//                .build();
//
//        transactionAcceptanceProducer.sendTransactionToAccept(transactionToAcceptance);
//
//        return this.createTransaction(transaction);
//    }
}
