package org.example.bank.repository.account;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.Account;
import org.example.bank.entity.QAccount;;


@RequiredArgsConstructor
public class AccountRepositoryCustomImpl implements AccountRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Account findByAccountNumber(String accountNumber) {
        QAccount qAccount = QAccount.account;

        Tuple tuple = queryFactory
                .select(qAccount.id,
                        qAccount.accountNumber,
                        qAccount.balance,
                        qAccount.accountHolderName,
                        qAccount.createdAt
                )
                .from(qAccount)
                .where(qAccount.accountNumber.eq(accountNumber))
                .fetchOne();

        return Account.builder()
                .id(tuple.get(qAccount.id))
                .accountNumber(tuple.get(qAccount.accountNumber))
                .balance(tuple.get(qAccount.balance))
                .accountHolderName(tuple.get(qAccount.accountHolderName))
                .createdAt(tuple.get(qAccount.createdAt))
                .build();
    }
}
