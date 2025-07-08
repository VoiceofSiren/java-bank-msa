package org.example.bank.repository.account;

import org.example.bank.entity.Account;

public interface AccountRepositoryCustom {
    Account findByAccountNumber(String accountNumber);
}
