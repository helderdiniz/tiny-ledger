package com.github.helderdiniz.ledger.domain;

import com.github.helderdiniz.ledger.exception.InsufficientFundsException;

import java.time.OffsetDateTime;

import static com.github.helderdiniz.ledger.domain.TransactionType.DEPOSIT;

public record Balance(Money amount,
                      OffsetDateTime lastCalculationDate) {
    public static Balance empty() {
        return new Balance(Money.empty(), OffsetDateTime.now());
    }

    public Balance apply(final Transaction transaction) {
        final var updated = transaction.getType().is(DEPOSIT)
                ? amount.add(transaction.getAmount())
                : amount.subtract(transaction.getAmount());

        if (updated.isNegative()) {
            throw new InsufficientFundsException("Insufficient funds: balance %s, attempted %s".formatted(amount, transaction.getAmount()));
        }

        return new Balance(updated, OffsetDateTime.now());
    }
}
