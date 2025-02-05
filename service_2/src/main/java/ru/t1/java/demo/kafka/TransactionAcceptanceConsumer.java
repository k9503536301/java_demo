package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptanceConsumer {
    private final TransactionService transactionService;

    @KafkaListener(
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.transaction_accept}",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void consumeTransactionMessage(TransactionAcceptanceDto transactionDto){
        log.debug("Transaction consumer: start handler");
        try {
            log.info("transaction: {}", transactionDto);
//            Transaction createdTransaction = transactionService.acceptTransaction(transactionDto);

//            log.info("Created Transaction: {}", createdTransaction);
        } catch (Exception e) {
            log.error("Failed to save transaction: {}", e.getMessage());
        }

        log.debug("Transaction consumer: end handler");
    }
}
