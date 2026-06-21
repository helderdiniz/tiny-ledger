package com.github.helderdiniz.ledger.repository.inmemory;

import com.github.helderdiniz.ledger.domain.Balance;
import com.github.helderdiniz.ledger.repository.BalanceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryBalanceRepository implements BalanceRepository {
    private volatile Balance balance;

    @Override
    public Balance save(final Balance balance) {
        this.balance = balance;
        return this.balance;
    }

    @Override
    public Optional<Balance> getCurrent() {
        return Optional.ofNullable(this.balance);
    }

    @Override
    public void deleteAll() {
        this.balance = null;
    }
}
