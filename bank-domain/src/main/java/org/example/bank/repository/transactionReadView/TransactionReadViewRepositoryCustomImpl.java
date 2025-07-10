package org.example.bank.repository.transactionReadView;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.QTransactionReadView;
import org.example.bank.entity.TransactionReadView;
import org.example.bank.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TransactionReadViewRepositoryCustomImpl implements TransactionReadViewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TransactionReadView> findByAccountIdOrderByCreatedAtDesc(Long accountId) {

        QTransactionReadView qTransactionReadView = QTransactionReadView.transactionReadView;

        JPAQuery<Tuple> tuples = queryFactory
                .select(qTransactionReadView.id,
                        qTransactionReadView.accountId,
                        qTransactionReadView.accountNumber,
                        qTransactionReadView.accountHolderName,
                        qTransactionReadView.type,
                        qTransactionReadView.amount,
                        qTransactionReadView.description,
                        qTransactionReadView.createdAt,
                        qTransactionReadView.balanceAfter)
                .from(qTransactionReadView)
                .where(qTransactionReadView.accountId.eq(accountId))
                .fetchAll();

        return mapToListFrom(qTransactionReadView, tuples);
    }

    @Override
    public List<TransactionReadView> findByAccountNumberOrderByCreatedAtDesc(String accountNumber) {

        QTransactionReadView qTransactionReadView = QTransactionReadView.transactionReadView;

        JPAQuery<Tuple> tuples = queryFactory
                .select(qTransactionReadView.id,
                        qTransactionReadView.accountId,
                        qTransactionReadView.accountNumber,
                        qTransactionReadView.accountHolderName,
                        qTransactionReadView.type,
                        qTransactionReadView.amount,
                        qTransactionReadView.description,
                        qTransactionReadView.createdAt,
                        qTransactionReadView.balanceAfter)
                .from(qTransactionReadView)
                .where(qTransactionReadView.accountNumber.eq(accountNumber))
                .fetchAll();
        return mapToListFrom(qTransactionReadView, tuples);
    }

    @Override
    public List<TransactionReadView> findByAccountIdAndTypeOrderByCreatedAtDesc(Long accountId, TransactionType type) {

        QTransactionReadView qTransactionReadView = QTransactionReadView.transactionReadView;

        JPAQuery<Tuple> tuples = queryFactory
                .select(qTransactionReadView.id,
                        qTransactionReadView.accountId,
                        qTransactionReadView.accountNumber,
                        qTransactionReadView.accountHolderName,
                        qTransactionReadView.type,
                        qTransactionReadView.amount,
                        qTransactionReadView.description,
                        qTransactionReadView.createdAt,
                        qTransactionReadView.balanceAfter)
                .from(qTransactionReadView)
                .where(qTransactionReadView.accountId.eq(accountId)
                        .and(
                                qTransactionReadView.type.eq(type)
                        ))
                .fetchAll();

        return mapToListFrom(qTransactionReadView, tuples);
    }

    @Override
    public List<TransactionReadView> findByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {

        QTransactionReadView qTransactionReadView = QTransactionReadView.transactionReadView;

        JPAQuery<Tuple> tuples = queryFactory
                .select(qTransactionReadView.id,
                        qTransactionReadView.accountId,
                        qTransactionReadView.accountNumber,
                        qTransactionReadView.accountHolderName,
                        qTransactionReadView.type,
                        qTransactionReadView.amount,
                        qTransactionReadView.description,
                        qTransactionReadView.createdAt,
                        qTransactionReadView.balanceAfter)
                .from(qTransactionReadView)
                .where(qTransactionReadView.accountId.eq(accountId)
                        .and(
                                qTransactionReadView.createdAt.between(startDate, endDate)
                        ))
                .fetchAll();

        return mapToListFrom(qTransactionReadView, tuples);
    }

    @Override
    public BigDecimal sumAmountByAccountIdAndType(Long accountId, TransactionType type) {

        QTransactionReadView qTransactionReadView = QTransactionReadView.transactionReadView;

        JPAQuery<Tuple> tuples = queryFactory
                .select(qTransactionReadView.id,
                        qTransactionReadView.accountId,
                        qTransactionReadView.accountNumber,
                        qTransactionReadView.accountHolderName,
                        qTransactionReadView.type,
                        qTransactionReadView.amount,
                        qTransactionReadView.description,
                        qTransactionReadView.createdAt,
                        qTransactionReadView.balanceAfter)
                .from(qTransactionReadView)
                .where(qTransactionReadView.accountId.eq(accountId)
                        .and(
                                qTransactionReadView.type.eq(type)
                        ))
                .fetchAll();

        return BigDecimal.valueOf(tuples.stream().count());
    }

    private List<TransactionReadView> mapToListFrom(QTransactionReadView qTransactionReadView, JPAQuery<Tuple> tuples) {
        return tuples.stream()
                .map(tuple -> TransactionReadView.builder()
                        .id(tuple.get(qTransactionReadView.id))
                        .accountId(tuple.get(qTransactionReadView.accountId))
                        .accountNumber(tuple.get(qTransactionReadView.accountNumber))
                        .accountHolderName(tuple.get(qTransactionReadView.accountHolderName))
                        .type(tuple.get(qTransactionReadView.type))
                        .amount(tuple.get(qTransactionReadView.amount))
                        .description(tuple.get(qTransactionReadView.description))
                        .createdAt(tuple.get(qTransactionReadView.createdAt))
                        .balanceAfter(tuple.get(qTransactionReadView.balanceAfter))
                        .build())
                .toList();
    }


}
