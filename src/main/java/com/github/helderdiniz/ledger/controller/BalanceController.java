package com.github.helderdiniz.ledger.controller;

import com.github.helderdiniz.ledger.api.facade.BalanceApi;
import com.github.helderdiniz.ledger.api.model.BalanceDTO;
import com.github.helderdiniz.ledger.service.query.BalanceQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController implements BalanceApi {

    private final BalanceQueryService balanceQueryService;

    public BalanceController(final BalanceQueryService balanceQueryService) {
        this.balanceQueryService = balanceQueryService;
    }

    @Override
    public ResponseEntity<BalanceDTO> getBalance() {
        final var currentBalance = balanceQueryService.getCurrent();
        return ResponseEntity.ok(currentBalance);
    }
}
