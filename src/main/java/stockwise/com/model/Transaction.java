package com.stockwise.model;

public record Transaction(
        int transactionId,
        int productId,
        int userId,
        String transactionType,
        int quantity,
        String transactionDate
) {}