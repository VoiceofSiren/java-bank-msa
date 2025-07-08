package org.example.bank.repository.accountReadView;

import org.example.bank.entity.AccountReadView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountReadViewRepository extends JpaRepository<AccountReadView, Long>, AccountReadViewRepositoryCustom {
}
