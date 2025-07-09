package org.example.bank.repository.accountReadView;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.Account;
import org.example.bank.entity.AccountReadView;
import org.example.bank.entity.QAccountReadView;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountReadViewRepositoryCustomImpl implements AccountReadViewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    QAccountReadView qAccountReadView = QAccountReadView.accountReadView;

    @Override
    public Optional<AccountReadView> findByAccountNumber(String accountNumber) {

        Tuple tuple = queryFactory
                .select(qAccountReadView.id,
                        qAccountReadView.accountNumber,
                        qAccountReadView.accountHolderName,
                        qAccountReadView.balance,
                        qAccountReadView.createdAt,
                        qAccountReadView.updatedAt,
                        qAccountReadView.transactionCount,
                        qAccountReadView.totalDeposits,
                        qAccountReadView.totalWithdrawals
                )
                .from(qAccountReadView)
                .where(qAccountReadView.accountNumber.eq(accountNumber))
                .fetchOne();

        AccountReadView accountReadView = AccountReadView.builder()
                .id(tuple.get(qAccountReadView.id))
                .accountNumber(tuple.get(qAccountReadView.accountNumber))
                .accountHolderName(tuple.get(qAccountReadView.accountHolderName))
                .balance(tuple.get(qAccountReadView.balance))
                .createdAt(tuple.get(qAccountReadView.createdAt))
                .updatedAt(tuple.get(qAccountReadView.updatedAt))
                .transactionCount(tuple.get(qAccountReadView.transactionCount))
                .totalDeposits(tuple.get(qAccountReadView.totalDeposits))
                .totalWithdrawals(tuple.get(qAccountReadView.totalWithdrawals))
                .build();
        return Optional.of(accountReadView);
    }
}
