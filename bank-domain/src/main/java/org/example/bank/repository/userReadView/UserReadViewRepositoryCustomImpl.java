package org.example.bank.repository.userReadView;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserReadViewRepositoryCustomImpl implements UserReadViewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

//    @Override
//    public User findByEmail(String email) {
//
//    }
}
