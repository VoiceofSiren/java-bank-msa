package org.example.bank.repository.transactionReadView;

import org.example.bank.entity.TransactionReadView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionReadViewRepository extends JpaRepository<TransactionReadView, Long>, TransactionReadViewRepositoryCustom {
}
