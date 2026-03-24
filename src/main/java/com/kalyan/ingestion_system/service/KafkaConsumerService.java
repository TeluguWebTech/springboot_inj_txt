package com.kalyan.ingestion_system.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "file-events", groupId = "ingestion-group")
    public void consume(String message) {

        System.out.println("Received from Kafka: " + message);

    }
}