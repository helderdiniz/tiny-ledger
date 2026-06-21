package com.github.helderdiniz.ledger.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// DDD Value Object.
// Kinda overkill for now, but allows for extensibility in the future (e.g. add 'CURRENCY')
public record Money(BigDecimal value) {
    public Money {
        Objects.requireNonNull(value, "value");
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money empty() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(final Money other) {
        return new Money(value.add(other.value));
    }

    public Money subtract(final Money other) {
        return new Money(value.subtract(other.value));
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public boolean isNegativeOrZero() {
        return value.signum() <= 0;
    }
}
