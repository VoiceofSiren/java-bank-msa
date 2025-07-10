package org.example.bank.exception;

public class AccountNotFoundException extends BankingException{

    public AccountNotFoundException(String accountNumber) {
        super("Account " + accountNumber + " not found");
    }

    public AccountNotFoundException(String accountNumber, Throwable cause) {
        super("Account " + accountNumber + " not found", cause);
    }
}
