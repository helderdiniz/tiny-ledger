package com.github.helderdiniz.ledger.repository;

import com.github.helderdiniz.ledger.domain.Balance;

import java.util.Optional;

public interface BalanceRepository {
    Balance save(Balance balance);

    Optional<Balance> getCurrent();

    void deleteAll();
}
