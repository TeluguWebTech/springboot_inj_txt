package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.FileEventDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "file-events", groupId = "ingestion-group")
    public void consume(FileEventDTO event) {

        System.out.println("JSON EVENT RECEIVED:");
        System.out.println("FileId: " + event.getFileId());
        System.out.println("Success: " + event.getSuccess());
        System.out.println("Failed: " + event.getFailed());
        System.out.println("Status: " + event.getStatus());
    }
}