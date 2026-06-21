package com.github.helderdiniz.ledger.mapper;

import com.github.helderdiniz.ledger.api.model.TransactionTypeDTO;
import com.github.helderdiniz.ledger.domain.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapper {
    public TransactionTypeDTO toDto(final TransactionType type) {
        return switch (type) {
            case DEPOSIT -> TransactionTypeDTO.DEPOSIT;
            case WITHDRAWAL -> TransactionTypeDTO.WITHDRAWAL;
        };
    }

    public TransactionType fromDto(final TransactionTypeDTO type) {
        return switch (type) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAWAL -> TransactionType.WITHDRAWAL;
        };
    }
}
