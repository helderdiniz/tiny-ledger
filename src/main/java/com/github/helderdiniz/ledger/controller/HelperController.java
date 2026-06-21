package com.github.helderdiniz.ledger.controller;

import com.github.helderdiniz.ledger.domain.Money;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import com.github.helderdiniz.ledger.service.command.BalanceCommandService;
import com.github.helderdiniz.ledger.service.query.BalanceQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.github.helderdiniz.ledger.domain.TransactionType.DEPOSIT;
import static com.github.helderdiniz.ledger.domain.TransactionType.WITHDRAWAL;

/**
 * Some endpoints to help out while manual testing the solution, e.g. to populate the in-memory store.
 * <p>
 * Not created via Openapi spec so I can hide it from Swagger UI.
 */
@RestController
public class HelperController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperController.class);

    private final TransactionRepository transactionRepository;
    private final BalanceCommandService balanceCommandService;
    private final BalanceQueryService balanceQueryService;

    public HelperController(final TransactionRepository transactionRepository,
                            final BalanceCommandService balanceCommandService,
                            final BalanceQueryService balanceQueryService) {
        this.transactionRepository = transactionRepository;
        this.balanceCommandService = balanceCommandService;
        this.balanceQueryService = balanceQueryService;
    }

    @PostMapping("/helper/transaction/populate")
    public void populateSomeTransactions() {
        final var pastDate = OffsetDateTime.now()
                .minusDays(15);

        IntStream.range(0, 15).forEach(index -> {
            final var type = index % 2 == 0 ? DEPOSIT : WITHDRAWAL;
            final var factor = type.is(DEPOSIT) ? 12L : 0L;

            final var transaction = transactionRepository.save(
                    Transaction.Builder.newBuilder()
                            .referenceId(UUID.randomUUID())
                            .amount(new Money(BigDecimal.valueOf(100 + (index * factor), 2)))
                            .date(pastDate.plusDays(index))
                            .type(index % 2 == 0 ? DEPOSIT : WITHDRAWAL)
                            .description("Populated via Helper Endpoint :)")
                            .build()
            );
            balanceCommandService.update(transaction);
            final var balance = balanceQueryService.getCurrent();

            LOGGER.info("[Helper] Created transaction {} {}, balance: {}", transaction.getType(), transaction.getAmount(), balance.getAmount());
        });
    }
}
