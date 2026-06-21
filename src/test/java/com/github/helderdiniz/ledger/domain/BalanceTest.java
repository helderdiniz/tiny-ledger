package com.github.helderdiniz.ledger.domain;

import com.github.helderdiniz.ledger.TestDataUtil;
import com.github.helderdiniz.ledger.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import static com.github.helderdiniz.ledger.TestDataUtil.ONE_EUR;
import static com.github.helderdiniz.ledger.TestDataUtil.TWO_EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BalanceTest {

    @Test
    void deposit() {
        final var ledger = Balance.empty();
        final var deposit = Transaction.Builder.newBuilder()
                .type(TransactionType.DEPOSIT)
                .amount(TWO_EUR)
                .build();
        final var result = ledger.apply(deposit);

        assertEquals(TWO_EUR, result.amount());
    }

    @Test
    void withdraw() {
        final var ledger = TestDataUtil.buildBalance(TWO_EUR);
        final var deposit = Transaction.Builder.newBuilder()
                .type(TransactionType.WITHDRAWAL)
                .amount(ONE_EUR)
                .build();

        final var result = ledger.apply(deposit);
        assertEquals(ONE_EUR, result.amount());
    }

    @Test
    void withdrawMoreThanWhatsAvailable() {
        final var ledger = TestDataUtil.buildBalance(ONE_EUR);
        final var deposit = Transaction.Builder.newBuilder()
                .type(TransactionType.WITHDRAWAL)
                .amount(TWO_EUR)
                .build();

        assertThrows(InsufficientFundsException.class, () -> ledger.apply(deposit));
    }

    @Test
    void withdrawToZero() {
        final var ledger = TestDataUtil.buildBalance(TWO_EUR);
        final var deposit = Transaction.Builder.newBuilder()
                .type(TransactionType.WITHDRAWAL)
                .amount(TWO_EUR)
                .build();

        final var result = ledger.apply(deposit);
        assertEquals(Money.empty(), result.amount());
    }

    @Test
    void withdrawWithEmptyLedger() {
        final var ledger = Balance.empty();
        final var deposit = Transaction.Builder.newBuilder()
                .type(TransactionType.WITHDRAWAL)
                .amount(TWO_EUR)
                .build();

        assertThrows(InsufficientFundsException.class, () -> ledger.apply(deposit));
    }
}
