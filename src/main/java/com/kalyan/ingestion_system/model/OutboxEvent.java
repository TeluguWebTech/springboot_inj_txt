package com.kalyan.ingestion_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status; 

    private LocalDateTime createdAt;
}