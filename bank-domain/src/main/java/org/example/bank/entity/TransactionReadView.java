package org.example.bank.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_read_views")
public class TransactionReadView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private Long accountId = 0L;

    @Column(nullable = false)
    private String accountNumber = "";

    @Column(nullable = false)
    private String acountHolderName = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type = TransactionType.DEPOSIT;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter = BigDecimal.ZERO;
}
