package org.example.bank.repository.accountReadView;

import org.example.bank.entity.AccountReadView;

import java.util.Optional;

public interface AccountReadViewRepositoryCustom {
    Optional<AccountReadView> findByAccountNumber(String accountNumber);
}
