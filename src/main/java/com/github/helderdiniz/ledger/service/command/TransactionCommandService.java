package com.github.helderdiniz.ledger.service.command;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.domain.Money;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.domain.TransactionCreationResult;
import com.github.helderdiniz.ledger.mapper.TransactionTypeMapper;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TransactionCommandService {
    private final TransactionRepository transactionRepository;
    private final TransactionTypeMapper transactionTypeMapper;
    private final BalanceCommandService balanceCommandService;
    private final ReentrantLock lock;

    public TransactionCommandService(final TransactionRepository transactionRepository,
                                     final TransactionTypeMapper transactionTypeMapper,
                                     final BalanceCommandService balanceCommandService) {
        this.transactionRepository = transactionRepository;
        this.transactionTypeMapper = transactionTypeMapper;
        this.balanceCommandService = balanceCommandService;
        this.lock = new ReentrantLock();
    }

    public TransactionCreationResult createTransaction(final CreateTransactionDTO createTransactionDTO) {
        var possibleTransaction = transactionRepository.getByReferenceId(createTransactionDTO.getReferenceId());
        if (possibleTransaction.isPresent()) {
            return TransactionCreationResult.idempotent(possibleTransaction.get());
        }

        lock.lock();
        try {
            // double-checked locking to avoid race condition here
            possibleTransaction = transactionRepository.getByReferenceId(createTransactionDTO.getReferenceId());
            if (possibleTransaction.isPresent()) {
                return TransactionCreationResult.idempotent(possibleTransaction.get());
            }

            final var transactionType = transactionTypeMapper.fromDto(createTransactionDTO.getType());
            final var transactionToPersist = Transaction.Builder.newBuilder()
                    .referenceId(createTransactionDTO.getReferenceId())
                    .amount(new Money(createTransactionDTO.getAmount()))
                    .date(OffsetDateTime.now())
                    .type(transactionType)
                    .description(createTransactionDTO.getDescription())
                    .build();

            balanceCommandService.update(transactionToPersist);
            final var persisted = transactionRepository.save(transactionToPersist);
            return TransactionCreationResult.created(persisted);
        } finally {
            lock.unlock();
        }
    }
}
