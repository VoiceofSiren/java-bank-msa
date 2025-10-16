package org.example.bank.repository.user;

import org.example.bank.entity.User;

public interface UserRepositoryCustom {
    User findByEmail(String email);
}
