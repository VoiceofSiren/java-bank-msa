package org.example.bank.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.bank.entity.QUser;
import org.example.bank.entity.User;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public User findByEmail(String email) {
        QUser qUser = QUser.user;

        return null;
    }
}
