package org.example.bank.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class TransactionAdvice implements TransactionRunner{

    @Override
    @Transactional
    public <T> T run(Supplier<T> func) {
        return func.get();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T readOnly(Supplier<T> func) {
        return func.get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T runNew(Supplier<T> func) {
        return func.get();
    }
}
