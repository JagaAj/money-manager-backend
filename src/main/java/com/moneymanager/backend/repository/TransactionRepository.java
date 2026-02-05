package com.moneymanager.backend.repository;

import com.moneymanager.backend.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
