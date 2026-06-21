package com.github.helderdiniz.ledger.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(final String message) {
        super(message);
    }
}
