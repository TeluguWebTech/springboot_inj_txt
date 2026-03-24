package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.model.OutboxEvent;
import com.kalyan.ingestion_system.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    public void processOutbox() {

        List<OutboxEvent> events = outboxRepository.findByStatus("NEW");

        for (OutboxEvent event : events) {

            try {
                // send to Kafka
                kafkaTemplate.send("file-events", event.getPayload());

                // mark processed
                event.setStatus("PROCESSED");
                outboxRepository.save(event);

                System.out.println("✅ Sent to Kafka: " + event.getPayload());

            } catch (Exception e) {
                System.out.println("❌ Kafka failed for event: " + event.getId());
            }
        }
    }
}