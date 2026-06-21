package com.github.helderdiniz.ledger.service.query;

import com.github.helderdiniz.ledger.api.model.TransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionListDTO;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.mapper.TransactionMapper;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionQueryService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionQueryService(final TransactionRepository transactionRepository,
                                   final TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionListDTO findAllPaginated(final Integer limit,
                                               final Integer page) {
        final var items = transactionRepository.findPaginated(limit, page)
                .stream()
                .map(transactionMapper::toDto)
                .toList();

        return new TransactionListDTO()
                .items(items);
    }

    public TransactionDTO map(final Transaction transaction) {
        return transactionMapper.toDto(transaction);
    }
}
