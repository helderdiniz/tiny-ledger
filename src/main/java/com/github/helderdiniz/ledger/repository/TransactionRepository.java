package com.github.helderdiniz.ledger.repository;

import com.github.helderdiniz.ledger.domain.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    List<Transaction> findAll();

    List<Transaction> findPaginated(Integer limit, Integer page);

    Transaction save(Transaction transaction);

    Optional<Transaction> getByReferenceId(UUID referenceId);

    void deleteAll();

    long countAll();
}
