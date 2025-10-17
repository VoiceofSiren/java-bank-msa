package org.example.bank.repository.userReadView;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.QUser;
import org.example.bank.entity.QUserReadView;
import org.example.bank.entity.UserReadView;

import java.util.Optional;

@RequiredArgsConstructor
public class UserReadViewRepositoryCustomImpl implements UserReadViewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    QUserReadView qUserReadView = QUserReadView.userReadView;

    @Override
    public Optional<UserReadView> findByEmail(String email) {

        Tuple tuple = queryFactory
                .select(qUserReadView.id,
                        qUserReadView.username,
                        qUserReadView.email,
                        qUserReadView.createdAt,
                        qUserReadView.updatedAt,
                        qUserReadView.accountCount,
                        qUserReadView.totalBalance
                )
                .from(qUserReadView)
                .where(qUserReadView.email.eq(email))
                .fetchOne();

        UserReadView userReadView = UserReadView.builder()
                .id(tuple.get(qUserReadView.id))
                .username(tuple.get(qUserReadView.username))
                .email(tuple.get(qUserReadView.email))
                .createdAt(tuple.get(qUserReadView.createdAt))
                .updatedAt(tuple.get(qUserReadView.updatedAt))
                .accountCount(tuple.get(qUserReadView.accountCount))
                .totalBalance(tuple.get(qUserReadView.totalBalance))
                .build();


        return Optional.of(userReadView);
    }
}
