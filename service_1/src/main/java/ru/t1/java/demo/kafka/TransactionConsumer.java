package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {
    private final TransactionService transactionService;

    @KafkaListener(
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "$(t1.kafka.topic.transactions)",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void consumeTransactionMessage(TransactionDto transactionDto){
        log.debug("Transaction consumer: start handler");
        try {
            Transaction createdTransaction = transactionService.acceptTransaction(transactionDto);

            log.info("Created Transaction: {}", createdTransaction);
        } catch (Exception e) {
            log.error("Failed to save transaction: {}", e.getMessage());
        }

        log.debug("Transaction consumer: end handler");
    }

    @KafkaListener(
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "$(t1.kafka.topic.transaction_result)",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void consumeTransactionProcessingResultMessage(TransactionResultDto transactionDto){
        log.debug("Transaction processing result consumer: start handler");
        try {
            transactionService.processTransaction(transactionDto);

            log.info("Processing Transaction: {}", transactionDto.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to save transaction: {}", e.getMessage());
        }

        log.debug("Transaction processing result: end handler");
    }
}
