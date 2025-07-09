package org.example.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_read_views")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountReadView implements Serializable {

    @Id
    private Long id = 0L;

    @Column(nullable = false)
    private String accountNumber = "";

    @Column(nullable = false)
    private String accountHolderName = "";

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 아래의 필드들은 읽기 최적화를 위해 추가
    @Column(nullable = false)
    private Integer transactionCount = 0;

    @Column(nullable = false)
    private BigDecimal totalDeposits = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalWithdrawals = BigDecimal.ZERO;
}
