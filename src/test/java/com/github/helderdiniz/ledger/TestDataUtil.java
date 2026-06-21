package com.github.helderdiniz.ledger;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionTypeDTO;
import com.github.helderdiniz.ledger.domain.Balance;
import com.github.helderdiniz.ledger.domain.Money;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.domain.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class TestDataUtil {
    public static final Money TWO_EUR = new Money(BigDecimal.valueOf(200, 2));
    public static final Money ONE_EUR = new Money(BigDecimal.valueOf(100, 2));

    private TestDataUtil() {
    }

    public static CreateTransactionDTO buildCreateTransactionDTO(final UUID reference,
                                                                 final TransactionTypeDTO type,
                                                                 final String amount) {
        return new CreateTransactionDTO()
                .referenceId(reference)
                .type(type)
                .amount(new BigDecimal(amount))
                .description("integration test");
    }

    public static Transaction buildTransaction(final UUID reference,
                                               final TransactionType type,
                                               final String amount) {
        return Transaction.Builder.newBuilder()
                .referenceId(reference)
                .type(type)
                .amount(new Money(new BigDecimal(amount)))
                .description("integration test")
                .date(OffsetDateTime.now())
                .build();
    }

    public static Balance buildBalance(final Money money) {
        return new Balance(money, OffsetDateTime.now());
    }

    public static Balance buildBalance(final String amount) {
        return buildBalance(new Money(new BigDecimal(amount)));
    }
}
