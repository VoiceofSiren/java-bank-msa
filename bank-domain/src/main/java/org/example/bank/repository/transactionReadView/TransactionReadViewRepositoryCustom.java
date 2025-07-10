package org.example.bank.repository.transactionReadView;

import org.example.bank.entity.TransactionReadView;
import org.example.bank.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionReadViewRepositoryCustom {
    List<TransactionReadView> findByAccountIdOrderByCreatedAtDesc(Long accountId);
    List<TransactionReadView> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
    List<TransactionReadView> findByAccountIdAndTypeOrderByCreatedAtDesc(Long accountId, TransactionType type);
    List<TransactionReadView> findByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    BigDecimal sumAmountByAccountIdAndType(Long accountId, TransactionType type);
}
