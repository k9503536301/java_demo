package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(accountService::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(@RequestBody AccountDto account) throws AccountException {
        try {
            Account newAccount = accountService.toEntity(account);
            Account savedAccount = accountService.createAccount(newAccount);
            return accountService.toDto(savedAccount);
        } catch (AccountException e) {
            throw new AccountException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountDto account) {
        Account accountToUpdate = accountService.toEntity(account);
        accountToUpdate.setId(id);
        Account updatedAccount = accountService.updateAccount(accountToUpdate);
        return new ResponseEntity<>(accountService.toDto(updatedAccount), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
