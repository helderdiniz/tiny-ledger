package com.github.helderdiniz.ledger.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void nullAmount() {
        assertThrows(NullPointerException.class, () -> new Money(null));
    }

    @Test
    void scalingAndRounding() {
        final var expected = "2.05";
        final var input = "2.0499999";

        final var result = new Money(new BigDecimal(input));
        assertEquals(expected, result.value().toString());
    }

    @Test
    void differentScalesAreEqual() {
        final var expected = new Money(new BigDecimal("2.0"));
        final var input = "2.00";

        final var result = new Money(new BigDecimal(input));
        assertEquals(expected, result);
    }

    @Test
    void add() {
        final var expected = new Money(new BigDecimal("4.00"));
        final var operand = new Money(new BigDecimal("2.00"));

        final var result = operand.add(operand);
        assertEquals(expected, result);
    }

    @Test
    void subtract() {
        final var expected = new Money(new BigDecimal("1.00"));
        final var operandOne = new Money(new BigDecimal("2.00"));
        final var operandTwo = new Money(new BigDecimal("1.00"));

        final var result = operandOne.subtract(operandTwo);
        assertEquals(expected, result);
    }
}
