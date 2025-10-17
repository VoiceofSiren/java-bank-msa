package org.example.bank.repository.userReadView;

import org.example.bank.entity.UserReadView;

import java.util.Optional;

public interface UserReadViewRepositoryCustom {
    Optional<UserReadView> findByEmail(String email);
}
