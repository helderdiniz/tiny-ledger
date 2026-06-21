package com.github.helderdiniz.ledger.domain;

public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT;

    public boolean is(final TransactionType type) {
        return type.equals(this);
    }
}
