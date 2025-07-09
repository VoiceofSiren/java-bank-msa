package org.example.bank.repository.transaction;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.Account;
import org.example.bank.entity.QTransaction;
import org.example.bank.entity.Transaction;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Transaction> findByAccountOrderByCreatedAtDesc(Account account) {
        QTransaction qTransaction = QTransaction.transaction;

        List<Tuple> tuples = queryFactory
                .select(
                        qTransaction.id,
                        qTransaction.account.id,
                        qTransaction.amount,
                        qTransaction.type,
                        qTransaction.description,
                        qTransaction.createdAt)
                .from(qTransaction)
                .where(qTransaction.account.id.eq(account.getId()))
                .orderBy(qTransaction.createdAt.desc())
                .fetch();

        return tuples.stream()
                .map(tuple -> Transaction.builder()
                        .id(tuple.get(qTransaction.id))
                        .account(account)
                        .amount(tuple.get(qTransaction.amount))
                        .type(tuple.get(qTransaction.type))
                        .description(tuple.get(qTransaction.description))
                        .createdAt(tuple.get(qTransaction.createdAt))
                        .build())
                .toList();
    }

    @Override
    public List<Transaction> findTopByAccountOrderByCreatedAtDesc(Account account, Pageable pageable) {
        QTransaction qTransaction = QTransaction.transaction;

        List<Tuple> tuples = queryFactory
                .select(
                        qTransaction.id,
                        qTransaction.account.id,
                        qTransaction.amount,
                        qTransaction.type,
                        qTransaction.description,
                        qTransaction.createdAt)
                .from(qTransaction)
                .where(qTransaction.account.id.eq(account.getId()))
                .orderBy(qTransaction.createdAt.desc())
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        return tuples.stream()
                .map(tuple -> Transaction.builder()
                        .id(tuple.get(qTransaction.id))
                        .account(account)
                        .amount(tuple.get(qTransaction.amount))
                        .type(tuple.get(qTransaction.type))
                        .description(tuple.get(qTransaction.description))
                        .createdAt(tuple.get(qTransaction.createdAt))
                        .build())
                .toList();
    }

}

