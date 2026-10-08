package com.taskflow.taskservice.service;

import com.taskflow.common.event.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final boolean kafkaEnabled;

    public TaskEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${taskflow.kafka.topics.task-events:task-events}") String topic,
            @Value("${taskflow.kafka.enabled:true}") boolean kafkaEnabled) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.kafkaEnabled = kafkaEnabled;
    }

    public void publishEvent(TaskEvent event) {
        if (!kafkaEnabled) {
            log.info("[Local / Kafka Disabled] Simulated Kafka Event: {}", event);
            return;
        }

        try {
            String key = event.getTaskId() != null ? event.getTaskId().toString() : "0";
            log.info("Publishing task event to Kafka topic '{}': {}", topic, event);
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("Failed to publish Kafka event: {}", ex.getMessage());
                } else {
                    log.info("Kafka event published successfully at offset {}", result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.warn("Kafka event publish skipped (broker possibly unreachable): {}", e.getMessage());
        }
    }
}
