package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionAcceptanceDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptanceProducer {
    private final KafkaTemplate kafkaTemplate;

    @Value("${t1.kafka.topic.transaction_accept}")
    private String transactionAcceptTopic;

    public void sendTransactionToAccept(TransactionAcceptanceDto transactionAcceptanceDto) {
        Message<TransactionAcceptanceDto> message = MessageBuilder.withPayload(transactionAcceptanceDto)
                .setHeader(KafkaHeaders.TOPIC, transactionAcceptTopic)
                .build();
        try {
            kafkaTemplate.send(message);
            log.info("Request transaction to accept. Transaction: {}", transactionAcceptanceDto.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to request transaction acceptance: {}", e.getMessage(), e);
        }
    }
}
