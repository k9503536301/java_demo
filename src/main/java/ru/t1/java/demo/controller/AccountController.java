package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Metric
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts()
                .stream()
                .map(accountService::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @Metric
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto account) {
        Account newAccount = accountService.toEntity(account);
        Account savedAccount = accountService.createAccount(newAccount);

        return new ResponseEntity<>(accountService.toDto(savedAccount), HttpStatus.CREATED);
    }

    @Metric
    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto account) {
        if (accountService.getAccountById(id).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Account accountToUpdate = accountService.toEntity(account);
        accountToUpdate.setId(id);
        Account updatedAccount = accountService.updateAccount(accountToUpdate);
        return new ResponseEntity<>(accountService.toDto(updatedAccount), HttpStatus.OK);
    }

    @Metric
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
