package com.moneymanager.backend.service;

import com.moneymanager.backend.model.Account;
import com.moneymanager.backend.model.Division;
import com.moneymanager.backend.model.Transaction;
import com.moneymanager.backend.model.TransactionType;
import com.moneymanager.backend.repository.AccountRepository;
import com.moneymanager.backend.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        transaction.setTimestamp(LocalDateTime.now());

        Account account = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if (transaction.getType() == TransactionType.TRANSFER) {
            Account toAccount = accountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
            account.setBalance(account.getBalance() - transaction.getAmount());
            toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
            accountRepository.save(toAccount);
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, Transaction updatedTransaction) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (existing.getTimestamp().isBefore(LocalDateTime.now().minusHours(12))) {
            throw new RuntimeException("Cannot edit transaction after 12 hours");
        }

        // Handle balance adjustments if amount or type changed
        // This is a bit complex for a simple MVP, so I'll keep it simple:
        // Just update fields that don't affect balance for now, or implement full
        // rollback.
        // For a high-quality app, I should handle balance changes.

        // Simpler approach: Rolling back the old transaction and applying the new one.
        rollbackTransaction(existing);

        existing.setAmount(updatedTransaction.getAmount());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setDivision(updatedTransaction.getDivision());
        existing.setDescription(updatedTransaction.getDescription());
        existing.setType(updatedTransaction.getType());
        existing.setToAccountId(updatedTransaction.getToAccountId());

        applyTransaction(existing);

        return transactionRepository.save(existing);
    }

    private void rollbackTransaction(Transaction transaction) {
        Account account = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else if (transaction.getType() == TransactionType.TRANSFER) {
            Account toAccount = accountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
            account.setBalance(account.getBalance() + transaction.getAmount());
            toAccount.setBalance(toAccount.getBalance() - transaction.getAmount());
            accountRepository.save(toAccount);
        }
        accountRepository.save(account);
    }

    private void applyTransaction(Transaction transaction) {
        Account account = accountRepository.findById(transaction.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        } else if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if (transaction.getType() == TransactionType.TRANSFER) {
            Account toAccount = accountRepository.findById(transaction.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
            account.setBalance(account.getBalance() - transaction.getAmount());
            toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
            accountRepository.save(toAccount);
        }
        accountRepository.save(account);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> filterTransactions(Division division, String category, LocalDateTime start,
            LocalDateTime end) {
        return transactionRepository.findAll().stream()
                .filter(t -> division == null || t.getDivision() == division)
                .filter(t -> category == null || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> start == null || t.getTimestamp().isAfter(start))
                .filter(t -> end == null || t.getTimestamp().isBefore(end))
                .collect(Collectors.toList());
    }
}
