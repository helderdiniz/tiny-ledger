package com.github.helderdiniz.ledger.service.query;

import com.github.helderdiniz.ledger.api.model.BalanceDTO;
import com.github.helderdiniz.ledger.domain.Balance;
import com.github.helderdiniz.ledger.mapper.BalanceMapper;
import com.github.helderdiniz.ledger.repository.BalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class BalanceQueryService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceQueryService(final BalanceRepository balanceRepository,
                               final BalanceMapper balanceMapper) {
        this.balanceRepository = balanceRepository;
        this.balanceMapper = balanceMapper;
    }

    public BalanceDTO getCurrent() {
        final var balance = balanceRepository.getCurrent()
                .orElseGet(Balance::empty);

        return balanceMapper.toDto(balance);
    }
}
