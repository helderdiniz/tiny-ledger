package com.github.helderdiniz.ledger.domain;

public record TransactionCreationResult(boolean created,
                                        Transaction transaction) {
    public static TransactionCreationResult created(Transaction transaction) {
        return new TransactionCreationResult(true, transaction);
    }

    public static TransactionCreationResult idempotent(Transaction transaction) {
        return new TransactionCreationResult(false, transaction);
    }
}
