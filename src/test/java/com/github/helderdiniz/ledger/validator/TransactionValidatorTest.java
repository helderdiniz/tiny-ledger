package com.github.helderdiniz.ledger.validator;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionTypeDTO;
import com.github.helderdiniz.ledger.exception.InvalidAmountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class TransactionValidatorTest {
    private TransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }

    @Test
    void passingValidation() {
        final var input = new CreateTransactionDTO()
                .type(TransactionTypeDTO.DEPOSIT)
                .referenceId(UUID.randomUUID())
                .amount(new BigDecimal("4.20"));

        validator.validateCreateTransaction(input);
    }

    @Test
    void nullAmount() {
        final var input = new CreateTransactionDTO()
                .amount(null);

        Assertions.assertThrows(InvalidAmountException.class, () -> validator.validateCreateTransaction(input));
    }

    @Test
    void emptyAmount() {
        final var input = new CreateTransactionDTO()
                .amount(BigDecimal.ZERO);

        Assertions.assertThrows(InvalidAmountException.class, () -> validator.validateCreateTransaction(input));
    }

    @Test
    void negativeAmount() {
        final var input = new CreateTransactionDTO()
                .amount(new BigDecimal("-3.10"));

        Assertions.assertThrows(InvalidAmountException.class, () -> validator.validateCreateTransaction(input));
    }

    @Test
    void positiveUnnormalizedScale() {
        final var input = new CreateTransactionDTO()
                .amount(new BigDecimal("0.0003"));

        Assertions.assertThrows(InvalidAmountException.class, () -> validator.validateCreateTransaction(input));
    }

    @Test
    void nullType() {
        final var input = new CreateTransactionDTO()
                .type(null)
                .referenceId(UUID.randomUUID())
                .amount(new BigDecimal("4.20"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreateTransaction(input));
    }

    @Test
    void nullReferenceId() {
        final var input = new CreateTransactionDTO()
                .type(TransactionTypeDTO.DEPOSIT)
                .referenceId(null)
                .amount(new BigDecimal("4.20"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateCreateTransaction(input));
    }
}
