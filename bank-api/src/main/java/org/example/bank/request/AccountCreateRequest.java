package org.example.bank.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountCreateRequest {
    private String userId;
    private String username;
    private BigDecimal initialBalance;
}
