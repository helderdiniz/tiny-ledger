package com.github.helderdiniz.ledger.repository.inmemory;

import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import com.github.helderdiniz.ledger.util.PaginationUtil;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<UUID, Transaction> transactions;

    public InMemoryTransactionRepository() {
        this.transactions = new ConcurrentHashMap<>();
    }

    @Override
    public List<Transaction> findAll() {
        return transactions.values()
                .stream()
                .toList();
    }

    @Override
    public List<Transaction> findPaginated(final Integer limit,
                                           final Integer page) {
        final var actualLimit = PaginationUtil.sanitizeLimit(limit);
        final var actualPage = PaginationUtil.sanitizePage(page);
        final var pager = PaginationUtil.calculateSkipAmount(actualLimit, actualPage);

        return transactions.values()
                .stream()
                .sorted(Comparator.comparing(Transaction::getDate))
                .skip(pager)
                .limit(actualLimit)
                .toList();
    }

    @Override
    public Transaction save(final Transaction transaction) {
        transactions.put(transaction.getReferenceId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> getByReferenceId(final UUID referenceId) {
        return Optional.ofNullable(transactions.get(referenceId));
    }

    @Override
    public void deleteAll() {
        this.transactions.clear();
    }

    @Override
    public long countAll() {
        return transactions.size();
    }
}
