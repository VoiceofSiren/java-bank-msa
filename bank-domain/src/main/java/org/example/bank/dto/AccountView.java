package org.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.bank.entity.Account;
import org.example.bank.entity.AccountReadView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountView implements Serializable {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountHolderName;
    private LocalDateTime createdAt;

    public static AccountView from(Account account) {
        return AccountView.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountHolderName(account.getAccountHolderName())
                .build();
    }

    public static AccountView fromReadView(AccountReadView accountReadView) {
        return AccountView.builder()
                .id(accountReadView.getId())
                .accountNumber(accountReadView.getAccountNumber())
                .balance(accountReadView.getBalance())
                .accountHolderName(accountReadView.getAccountHolderName())
                .createdAt(accountReadView.getCreatedAt())
                .build();
    }
}
