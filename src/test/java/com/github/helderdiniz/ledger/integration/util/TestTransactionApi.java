package com.github.helderdiniz.ledger.integration.util;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionListDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestTransactionApi {
    private final RestTemplate restTemplate;
    private final String basePath;

    public TestTransactionApi(final int port) {
        this.basePath = "http://localhost:" + port;
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<TransactionDTO> createTransaction(final CreateTransactionDTO dto) {
        final var url = basePath + "/transaction";
        return restTemplate.postForEntity(url, dto, TransactionDTO.class);
    }

    public ResponseEntity<TransactionListDTO> getAllTransactions() {
        final var url = basePath + "/transaction";
        return restTemplate.getForEntity(url, TransactionListDTO.class);
    }

    public ResponseEntity<TransactionListDTO> getPaginatedTransactions(final Integer limit,
                                                                       final Integer page) {
        final var url = basePath + "/transaction?limit=" + limit + "&page=" + page;
        return restTemplate.getForEntity(url, TransactionListDTO.class);
    }
}
