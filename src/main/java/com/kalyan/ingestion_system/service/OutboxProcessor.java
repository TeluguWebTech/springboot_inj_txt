package com.kalyan.ingestion_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyan.ingestion_system.dto.FileEventDTO;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    public void processOutbox() {

        List<OutboxEvent> events = outboxRepository.findByStatus("NEW");

        for (OutboxEvent event : events) {

            try {
                // send to Kafka
                ObjectMapper objectMapper = new ObjectMapper();
                FileEventDTO dto = objectMapper.readValue(event.getPayload(), FileEventDTO.class);

                kafkaTemplate.send("file-events", dto);

                // mark processed
                event.setStatus("PROCESSED");
                outboxRepository.save(event);

                System.out.println(" Sent to Kafka: " + event.getPayload());

            } catch (Exception e) {
                System.out.println(" Kafka failed for event: " + event.getId());
            }
        }
    }
}