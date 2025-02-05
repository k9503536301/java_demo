package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.kafka.TransactionProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    @Value("${t1.transaction.frequency-limit.interval}")
    private Long transactionInterval;
    @Value("${t1.transaction.frequency-limit.count}")
    private Long transactionCountLimit;

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    public TransactionResultDto toResultDto(Transaction entity) {
        return TransactionResultDto.builder()
                .accountId(entity.getAccount().getAccountId())
                .transactionId(entity.getTransactionId())
                .status(entity.getStatus())
                .build();
    }

    public TransactionResultDto toResultDto(TransactionAcceptanceDto acceptanceDto, TransactionStatus transactionStatus) {
        return TransactionResultDto.builder()
                .accountId(acceptanceDto.getAccountId())
                .transactionId(acceptanceDto.getTransactionId())
                .status(transactionStatus)
                .build();
    }

    public void processTransaction(TransactionAcceptanceDto acceptanceDto) {
        if(!checkLimitTransactionPerInterval(acceptanceDto)) {
            return;
        }

        TransactionStatus currentStatus = acceptanceDto.getAccountBalance().compareTo(acceptanceDto.getTransactionAmount()) < 0
                ? TransactionStatus.REJECTED
                : TransactionStatus.BLOCKED;

        transactionProducer.send(this.toResultDto(acceptanceDto, currentStatus));
    }

    private Boolean checkLimitTransactionPerInterval(TransactionAcceptanceDto acceptanceDto) {
        LocalDateTime startTime = acceptanceDto.getTimestamp();
        LocalDateTime endTime = startTime.minusSeconds(transactionInterval);
        List<Transaction> transactionsPerInterval = transactionRepository.findByAccountAndStatusBetweenDates(
                acceptanceDto.getAccountId(),
                startTime,
                endTime
        );

        if ((long) transactionsPerInterval.size() >= transactionCountLimit-1) {
            transactionsPerInterval.forEach(tx->{
                tx.setStatus(TransactionStatus.BLOCKED);
                transactionProducer.send(
                        this.toResultDto(tx)
                );
            });

            transactionProducer.send(this.toResultDto(acceptanceDto, TransactionStatus.BLOCKED));

            return false;
        }

        return true;
    }
}
