package org.example.bank.repository.accountReadView;

import org.example.bank.entity.AccountReadView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountReadViewRepository extends JpaRepository<AccountReadView, Long>, AccountReadViewRepositoryCustom {
}
