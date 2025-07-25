package org.example.bank.request;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
public class TransferRequest implements Serializable {
    @Parameter(description = "Source account number", required = true)
    private String fromAccountNumber;
    @Parameter(description = "Target account number", required = true)
    private String toAccountNumber;
    @Parameter(description = "Transfer amount", required = true)
    private BigDecimal amount;
}
