package org.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.TransactionReadView;
import org.example.bank.entity.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionView implements Serializable {
    private Long id;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime createdAt;
    private BigDecimal balanceAfter;

    public static TransactionView from(Transaction transaction) {
        return TransactionView.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .balanceAfter(transaction.getAccount().getBalance())
                .build();
    }

    public static TransactionView fromReadView(TransactionReadView transactionReadView) {
        return TransactionView.builder()
                .id(transactionReadView.getId())
                .accountId(transactionReadView.getAccountId())
                .accountNumber(transactionReadView.getAccountNumber())
                .amount(transactionReadView.getAmount())
                .type(transactionReadView.getType())
                .description(transactionReadView.getDescription())
                .createdAt(transactionReadView.getCreatedAt())
                .balanceAfter(transactionReadView.getBalanceAfter())
                .build();
    }
}
