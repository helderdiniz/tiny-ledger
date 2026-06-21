package com.github.helderdiniz.ledger.integration;

import com.github.helderdiniz.ledger.TestDataUtil;
import com.github.helderdiniz.ledger.integration.util.TestBalanceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BalanceIntegrationTest extends BaseIntegrationTest {
    private TestBalanceApi testBalanceApi;

    @BeforeEach
    void setup() {
        this.testBalanceApi = new TestBalanceApi(port);
    }

    @Test
    void getBalance_returnsCurrentBalance() {
        balanceRepository.save(TestDataUtil.buildBalance("250.00"));

        final var response = testBalanceApi.getBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertAmount("250.00", response.getBody().getAmount());
        assertNotNull(response.getBody().getLastCalculationDate());
    }

    @Test
    void getBalance_OnEmptyLedger_returnsBalanceZero() {
        final var response = testBalanceApi.getBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertAmount("0.00", response.getBody().getAmount());
        assertNotNull(response.getBody().getLastCalculationDate());
    }
}
