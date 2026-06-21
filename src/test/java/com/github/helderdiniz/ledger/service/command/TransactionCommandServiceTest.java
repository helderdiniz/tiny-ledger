package com.github.helderdiniz.ledger.service.command;

import com.github.helderdiniz.ledger.api.model.CreateTransactionDTO;
import com.github.helderdiniz.ledger.api.model.TransactionTypeDTO;
import com.github.helderdiniz.ledger.domain.Transaction;
import com.github.helderdiniz.ledger.domain.TransactionType;
import com.github.helderdiniz.ledger.mapper.TransactionTypeMapper;
import com.github.helderdiniz.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionCommandServiceTest {
    private TransactionCommandService transactionCommandService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionTypeMapper transactionTypeMapper;
    @Mock
    private BalanceCommandService balanceCommandService;

    @BeforeEach
    void setUp() {
        transactionCommandService = new TransactionCommandService(transactionRepository, transactionTypeMapper, balanceCommandService);
    }

    @Test
    void createDuplicateTransactionSequential() {
        final var uuid = UUID.randomUUID();
        final var input = new CreateTransactionDTO()
                .type(TransactionTypeDTO.DEPOSIT)
                .amount(BigDecimal.ONE)
                .referenceId(uuid);
        final var mockedTransaction = Transaction.Builder.newBuilder()
                .build();

        given(transactionTypeMapper.fromDto(TransactionTypeDTO.DEPOSIT))
                .willReturn(TransactionType.DEPOSIT);
        given(transactionRepository.getByReferenceId(uuid))
                .willReturn(Optional.empty()) // First call, first iteration
                .willReturn(Optional.empty()) // Second call, first iteration (double-check)
                .willReturn(Optional.of(mockedTransaction)); // Created, should proceed from now on
        given(transactionRepository.save(any()))
                .willReturn(mockedTransaction);

        IntStream.range(0, 10)
                .forEach(_ -> transactionCommandService.createTransaction(input));

        verify(transactionRepository, times(1)).save(any());
        verify(balanceCommandService, times(1)).update(any());
        verify(transactionRepository, times(11)).getByReferenceId(any());
    }
}
