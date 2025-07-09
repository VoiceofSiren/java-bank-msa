package org.example.bank.repository.transaction;

import org.example.bank.entity.Account;
import org.example.bank.entity.Transaction;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionRepositoryCustom {
    List<Transaction> findByAccountOrderByCreatedAtDesc(Account account);
    List<Transaction> findTopByAccountOrderByCreatedAtDesc(Account account, Pageable pageable);
}
