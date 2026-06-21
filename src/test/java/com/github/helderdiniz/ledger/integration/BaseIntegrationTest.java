package com.github.helderdiniz.ledger.integration;

import com.github.helderdiniz.ledger.Application;
import com.github.helderdiniz.ledger.repository.BalanceRepository;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class BaseIntegrationTest {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BalanceRepository balanceRepository;
    @Value("${local.server.port}")
    int port;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();
    }

    BigDecimal getCurrentBalance() {
        return balanceRepository.getCurrent()
                .get()
                .amount()
                .value();
    }

    void assertAmount(final String expected,
                      final BigDecimal actual) {
        assertNotNull(actual);
        assertEquals(0, new BigDecimal(expected).compareTo(actual), () -> "expected amount %s but was %s".formatted(expected, actual));
    }

    void awaitQuietly(final CountDownLatch latch) {
        try {
            latch.await();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
