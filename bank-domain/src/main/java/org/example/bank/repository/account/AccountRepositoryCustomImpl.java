package org.example.bank.repository.account;

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
        return null;
    }
}
