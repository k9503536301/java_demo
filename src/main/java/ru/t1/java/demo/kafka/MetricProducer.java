package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.MetricErrorType;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricProducer {
    private final KafkaTemplate kafkaTemplate;

    @Value("${t1.kafka.topic.metrics}")
    private String metricsTopic;

    public <T> void sendMetricLog(T metricLogDto, MetricErrorType metricType) {
        Message<T> message = MessageBuilder.withPayload(metricLogDto)
                .setHeader(KafkaHeaders.TOPIC, metricsTopic)
                .setHeader("errorType", metricType)
                .build();
        try {
            kafkaTemplate.send(message);
            log.info("Error message sent to Kafka topic: {}", metricsTopic);
        } catch (Exception e) {
            log.error("Failed to send metric data to Kafka: {}", e.getMessage(), e);
        }
    }
}
