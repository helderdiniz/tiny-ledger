package com.github.helderdiniz.ledger.validator;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.domain.Money;
import com.github.helderdiniz.ledger.exception.InvalidAmountException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class TransactionValidator {
    public void validateCreateTransaction(final CreateTransactionDTO createTransactionDTO) {
        if (isNull(createTransactionDTO.getAmount())) {
            throw new InvalidAmountException("Amount must not be null");
        }
        final var roundedAmount = new Money(createTransactionDTO.getAmount());
        if (roundedAmount.isNegativeOrZero()) {
            throw new InvalidAmountException("Rounded Amount must not be negative or zero");
        }

        if (isNull(createTransactionDTO.getType())) {
            throw new IllegalArgumentException("Transaction Type must not be null");
        }

        if (isNull(createTransactionDTO.getReferenceId())) {
            throw new IllegalArgumentException("ReferenceId must not be null");
        }
    }
}

