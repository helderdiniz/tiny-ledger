package com.github.helderdiniz.ledger.service.command;

import com.github.helderdiniz.ledger.domain.Balance;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.repository.BalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class BalanceCommandService {
    private final BalanceRepository balanceRepository;

    public BalanceCommandService(final BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public void update(final Transaction transaction) {
        final var toPersist = balanceRepository.getCurrent()
                .orElseGet(Balance::empty)
                .apply(transaction);
        balanceRepository.save(toPersist);
    }
}
