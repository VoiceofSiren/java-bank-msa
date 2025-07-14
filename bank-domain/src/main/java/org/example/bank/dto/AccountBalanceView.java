package org.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.bank.entity.Account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceView implements Serializable {
    private String accountNumber;
    private BigDecimal balance;
    private String accountHolderName;
    private LocalDateTime lastUpdated;

    public static AccountBalanceView from(Account account) {
        return AccountBalanceView.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountHolderName(account.getAccountHolderName())
                .lastUpdated(account.getCreatedAt())
                .build();
    }
}
