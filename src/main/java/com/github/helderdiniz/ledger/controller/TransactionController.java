package com.github.helderdiniz.ledger.controller;

import com.github.helderdiniz.ledger.api.facade.TransactionApi;
import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionListDTO;
import com.github.helderdiniz.ledger.service.command.TransactionCommandService;
import com.github.helderdiniz.ledger.service.query.TransactionQueryService;
import com.github.helderdiniz.ledger.validator.TransactionValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController implements TransactionApi {

    private final TransactionQueryService transactionQueryService;
    private final TransactionCommandService transactionCommandService;
    private final TransactionValidator transactionValidator;

    public TransactionController(final TransactionQueryService transactionQueryService,
                                 final TransactionCommandService transactionCommandService,
                                 final TransactionValidator transactionValidator) {
        this.transactionQueryService = transactionQueryService;
        this.transactionCommandService = transactionCommandService;
        this.transactionValidator = transactionValidator;
    }

    @Override
    public ResponseEntity<TransactionListDTO> getTransactions(final Integer limit,
                                                              final Integer page) {
        final var allPaginated = transactionQueryService.findAllPaginated(limit, page);
        return ResponseEntity.ok(allPaginated);
    }

    @Override
    public ResponseEntity<TransactionDTO> createTransaction(final CreateTransactionDTO createTransactionDTO) {
        transactionValidator.validateCreateTransaction(createTransactionDTO);

        final var transactionCommandResult = transactionCommandService.createTransaction(createTransactionDTO);
        final var transactionDto = transactionQueryService.map(transactionCommandResult.transaction());

        return ResponseEntity.status(transactionCommandResult.created() ?
                        HttpStatus.CREATED : // created right now, 201
                        HttpStatus.OK)       // previously creating, hint of idempotency, 200
                .body(transactionDto);
    }
}
