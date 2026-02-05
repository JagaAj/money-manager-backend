package com.moneymanager.backend.service;

import com.moneymanager.backend.model.Account;
import com.moneymanager.backend.repository.AccountRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(String id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
