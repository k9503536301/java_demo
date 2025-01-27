package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.MetricErrorType;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceErrorProducer {
    private final KafkaTemplate kafkaTemplate;

    @Value("${t1.kafka.topic.metrics}")
    private String metricsTopic;

    public void sendErrorLog(DataSourceErrorLog err) {
        Message<DataSourceErrorLog> message = MessageBuilder.withPayload(err)
                .setHeader(KafkaHeaders.TOPIC, metricsTopic)
                .setHeader("errorType", MetricErrorType.DATA_SOURCE)
                .build();

        kafkaTemplate.send(message);
        log.info("Error message sent to Kafka topic: {}", metricsTopic);
    }
}
