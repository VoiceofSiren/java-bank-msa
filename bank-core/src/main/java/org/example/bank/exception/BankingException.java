package org.example.bank.exception;

abstract class BankingException extends RuntimeException{

    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
    }

}
