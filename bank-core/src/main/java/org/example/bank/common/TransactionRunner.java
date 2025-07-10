package org.example.bank.common;

import java.util.function.Supplier;

public interface TransactionRunner {

    <T> T run(Supplier<T> func); // @Transactional

    <T> T readOnly(Supplier<T> func); // @Transactional(readOnly = true)

    <T> T runNew(Supplier<T> func); // @Transactional(propagation = Propagation.REQUIRES_NEW)
}

