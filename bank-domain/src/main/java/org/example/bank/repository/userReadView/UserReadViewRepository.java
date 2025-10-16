package org.example.bank.repository.userReadView;

import org.example.bank.entity.UserReadView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReadViewRepository extends JpaRepository<UserReadView, String>, UserReadViewRepositoryCustom {
}
