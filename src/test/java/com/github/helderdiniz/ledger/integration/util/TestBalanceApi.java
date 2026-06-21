package com.github.helderdiniz.ledger.integration.util;

import com.github.helderdiniz.ledger.api.model.BalanceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestBalanceApi {
    private final RestTemplate restTemplate;
    private final String basePath;

    public TestBalanceApi(final int port) {
        this.basePath = "http://localhost:" + port;
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<BalanceDTO> getBalance() {
        final var url = basePath + "/balance";
        return restTemplate.getForEntity(url, BalanceDTO.class);
    }
}
