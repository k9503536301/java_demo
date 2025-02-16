package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {
    private final KafkaTemplate kafkaTemplate;

    @Value("${t1.kafka.topic.transaction_result}")
    private String transactionResultTopic;

    public void send(TransactionResultDto transactionResultDto) {
        Message<TransactionResultDto> message = MessageBuilder.withPayload(transactionResultDto)
                .setHeader(KafkaHeaders.TOPIC, transactionResultTopic)
                .build();
        try {
            kafkaTemplate.send(message);
            log.info("Send result of processed transaction. Transaction: {}", transactionResultDto.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send processed transaction acceptance: {}", e.getMessage(), e);
        }
    }
}
