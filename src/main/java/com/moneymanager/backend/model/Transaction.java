package com.moneymanager.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String userId; // Link to user
    private TransactionType type; // INCOME, EXPENSE, TRANSFER
    private Double amount;
    private String category; // fuel, movie, food, loan, medical, etc.
    private Division division; // OFFICE, PERSONAL
    private String description;
    private LocalDateTime timestamp;
    private String fromAccountId;
    private String toAccountId; // For transfers

    public Transaction() {
    }

    public Transaction(String id, TransactionType type, Double amount, String category, Division division,
            String description, LocalDateTime timestamp, String fromAccountId, String toAccountId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.division = division;
        this.description = description;
        this.timestamp = timestamp;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
