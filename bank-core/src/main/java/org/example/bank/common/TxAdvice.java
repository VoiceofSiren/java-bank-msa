package org.example.bank.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TxAdvice {
    private final TransactionAdvice transactionAdvice;

    public <T> T run(Supplier<T> func) {
        return transactionAdvice.run(func);
    }

    public <T> T readOnly(Supplier<T> func) {
        return transactionAdvice.readOnly(func);
    }

    public <T> T runNew(Supplier<T> func) {
        return transactionAdvice.runNew(func);
    }

}
