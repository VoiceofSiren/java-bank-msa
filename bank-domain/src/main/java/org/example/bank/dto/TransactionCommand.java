package org.example.bank.dto;

import org.example.bank.entity.TransactionType;

import java.math.BigDecimal;

public class TransactionCommand {
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
}
