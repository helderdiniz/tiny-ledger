package com.github.helderdiniz.ledger.integration;

import com.github.helderdiniz.ledger.TestDataUtil;
import com.github.helderdiniz.ledger.api.model.TransactionTypeDTO;
import com.github.helderdiniz.ledger.domain.Balance;
import com.github.helderdiniz.ledger.domain.TransactionType;
import com.github.helderdiniz.ledger.integration.util.TestTransactionApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.helderdiniz.ledger.TestDataUtil.buildCreateTransactionDTO;
import static com.github.helderdiniz.ledger.api.model.TransactionTypeDTO.DEPOSIT;
import static org.junit.jupiter.api.Assertions.*;

class TransactionIntegrationTest extends BaseIntegrationTest {

    private TestTransactionApi transactionApi;

    @BeforeEach
    void setup() {
        this.transactionApi = new TestTransactionApi(port);
    }

    @Test
    void normalDeposit_returns201_andUpdatesBalance() {
        final var reference = UUID.randomUUID();
        final var request = buildCreateTransactionDTO(reference, DEPOSIT, "100.00");
        final var response = transactionApi.createTransaction(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(reference, response.getBody().getReferenceId());
        assertEquals(DEPOSIT, response.getBody().getType());
        assertAmount("100.00", response.getBody().getAmount());
        assertAmount("100.00", getCurrentBalance());
    }

    @Test
    void replayedDeposit_returns200_andAppliesBalanceOnce_withIdempotencyCheck() {
        final var reference = UUID.randomUUID();
        final var request = buildCreateTransactionDTO(reference, DEPOSIT, "100.00");

        final var expectedCreated = transactionApi.createTransaction(request);
        final var expectedIdempotent = transactionApi.createTransaction(request);

        assertEquals(HttpStatus.CREATED, expectedCreated.getStatusCode());
        assertEquals(HttpStatus.OK, expectedIdempotent.getStatusCode());

        assertEquals(reference, expectedIdempotent.getBody().getReferenceId());
        assertAmount("100.00", getCurrentBalance());
        assertEquals(1, transactionRepository.countAll());
    }

    @Test
    void normalWithdrawal_returns201_andDecrementsBalance() {
        // Prepare initial deposit
        final var initialDeposit = transactionRepository.save(TestDataUtil.buildTransaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                "100.00"
        ));
        balanceRepository.save(new Balance(initialDeposit.getAmount(), initialDeposit.getDate()));

        final var withdrawRequest = buildCreateTransactionDTO(UUID.randomUUID(), TransactionTypeDTO.WITHDRAWAL, "30.00");
        final var withdrawResponse = transactionApi.createTransaction(withdrawRequest);

        assertEquals(HttpStatus.CREATED, withdrawResponse.getStatusCode());
        assertAmount("70.00", getCurrentBalance());
    }

    @Test
    void withdrawalOnEmptyBalance_returns422_problemJson() {
        final var request = buildCreateTransactionDTO(UUID.randomUUID(), TransactionTypeDTO.WITHDRAWAL, "50.00");

        try {
            transactionApi.createTransaction(request);
            fail();
        } catch (final HttpClientErrorException exception) {
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, exception.getStatusCode());
            final var body = exception.getResponseBodyAs(ProblemDetail.class);
            assertTrue(body.getTitle().contains("Insufficient funds"));
            assertTrue(transactionRepository.findAll().isEmpty());
        }
    }

    @Test
    void zeroAmount_returns400() {
        final var request = buildCreateTransactionDTO(UUID.randomUUID(), DEPOSIT, "0.00");
        try {
            transactionApi.createTransaction(request);
            fail();
        } catch (HttpClientErrorException exception) {
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            final var body = exception.getResponseBodyAs(ProblemDetail.class);
            assertTrue(body.getTitle().contains("Invalid request"));
            assertTrue(transactionRepository.findAll().isEmpty());
        }
    }

    @Test
    void getTransactions_returnsCreatedItems_orderedByDate() {
        transactionRepository.save(TestDataUtil.buildTransaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                "10.00"
        ));
        transactionRepository.save(TestDataUtil.buildTransaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                "20.00"
        ));
        transactionRepository.save(TestDataUtil.buildTransaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                "30.00"
        ));

        final var items = transactionApi.getAllTransactions().getBody().getItems();

        assertEquals(3, items.size());
        assertAmount("10.00", items.get(0).getAmount());
        assertAmount("20.00", items.get(1).getAmount());
        assertAmount("30.00", items.get(2).getAmount());
    }

    @Test
    void getTransactions_paginated_returnsRequestedPage() {
        IntStream.rangeClosed(1, 5).forEach(i -> transactionRepository.save(TestDataUtil.buildTransaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                i + ".00"
        )));

        final var firstPage = transactionApi.getPaginatedTransactions(2, 0).getBody().getItems();
        final var secondPage = transactionApi.getPaginatedTransactions(2, 1).getBody().getItems();
        final var thirdPage = transactionApi.getPaginatedTransactions(2, 2).getBody().getItems();

        assertEquals(2, firstPage.size());
        assertEquals(2, secondPage.size());
        assertEquals(1, thirdPage.size());
        assertAmount("1.00", firstPage.get(0).getAmount());
        assertAmount("3.00", secondPage.get(0).getAmount());
        assertAmount("5.00", thirdPage.get(0).getAmount());
    }

    @Test
    void duplicateConcurrentCreate_createsExactlyOnce() {
        final var threadCount = 10;
        final var reference = UUID.randomUUID();
        final var request = buildCreateTransactionDTO(reference, DEPOSIT, "100.00");
        final List<HttpStatusCode> statuses = Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newFixedThreadPool(threadCount)) {
            final var startLatch = new CountDownLatch(1);

            final var futures = IntStream.range(0, threadCount)
                    .mapToObj(_ -> CompletableFuture.runAsync(() -> {
                        awaitQuietly(startLatch);
                        final var response = transactionApi.createTransaction(request);
                        statuses.add(response.getStatusCode());
                    }, executor)).toArray(CompletableFuture[]::new);

            startLatch.countDown();
            CompletableFuture.allOf(futures).join();
            executor.shutdown();
        }

        final var statusCounts = statuses.stream()
                .collect(Collectors.groupingBy(HttpStatusCode::value, Collectors.counting()));
        final var created = statusCounts.getOrDefault(HttpStatus.CREATED.value(), 0L);
        final var idempotent = statusCounts.getOrDefault(HttpStatus.OK.value(), 0L);

        // expect one created (201), the rest idempotent (200)
        assertEquals(1, created);
        assertEquals(threadCount - 1, idempotent);
        assertEquals(1, transactionRepository.countAll());
        assertAmount("100.00", getCurrentBalance());
    }
}
