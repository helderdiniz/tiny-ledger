package com.github.helderdiniz.ledger.mapper;

import com.github.helderdiniz.ledger.api.model.TransactionDTO;
import com.github.helderdiniz.ledger.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    private final TransactionTypeMapper transactionTypeMapper;

    public TransactionMapper(final TransactionTypeMapper transactionTypeMapper) {
        this.transactionTypeMapper = transactionTypeMapper;
    }

    public TransactionDTO toDto(final Transaction transaction) {
        return new TransactionDTO()
                .referenceId(transaction.getReferenceId())
                .amount(transaction.getAmount().value())
                .date(transaction.getDate())
                .type(transactionTypeMapper.toDto(transaction.getType()))
                .description(transaction.getDescription());
    }
}
