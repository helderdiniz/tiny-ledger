package com.github.helderdiniz.ledger.mapper;

import com.github.helderdiniz.ledger.api.model.BalanceDTO;
import com.github.helderdiniz.ledger.domain.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper {
    public BalanceDTO toDto(final Balance balance) {
        return new BalanceDTO()
                .amount(balance.amount().value())
                .lastCalculationDate(balance.lastCalculationDate());
    }
}
