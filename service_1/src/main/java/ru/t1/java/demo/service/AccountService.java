package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    @PostConstruct
    void init() {
        try {
            List<Account> accounts = parseJson();
            accountRepository.saveAll(accounts);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    public List<Account> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountDto[] accounts;

        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mock_data/accounts.json")){
            accounts = mapper.readValue(in, AccountDto[].class);
        } catch(Exception e){
            throw new IOException(e);
        }

        return Arrays.stream(accounts)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Account toEntity(AccountDto dto) {
        Client client = clientService.getClientById(dto.getClientId());

        return Account.builder()
                .accountId(dto.getAccountId())
                .client(client)
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .status(dto.getStatus())
                .frozenAmount(dto.getFrozenAmount())
                .build();
    }

    public AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .accountId(entity.getAccountId())
                .clientId(entity.getClient().getClientId())
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .frozenAmount(entity.getFrozenAmount())
                .build();
    }

    @LogDataSourceError
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Account not found"));
    }

    @LogDataSourceError
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @LogDataSourceError
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    public void deleteAccountById(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
