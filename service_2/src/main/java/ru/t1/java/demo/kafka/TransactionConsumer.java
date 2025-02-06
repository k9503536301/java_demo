package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;
import ru.t1.java.demo.service.TransactionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {
    private final TransactionService transactionService;

    @KafkaListener(
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "t1_demo_transaction_accept",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void consumeTransactionMessage(TransactionAcceptanceDto acceptanceDto){
        log.debug("Transaction consumer: start handler");
        try {
            log.info("transaction: {}", acceptanceDto);
            transactionService.processTransaction(acceptanceDto);
            log.info("Transaction was processed: {}", acceptanceDto);
        } catch (Exception e) {
            log.error("Failed to save transaction: {}", e.getMessage());
        }

        log.debug("Transaction consumer: end handler");
    }
}
