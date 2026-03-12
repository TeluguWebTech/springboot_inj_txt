package com.kalyan.ingestion_system.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String status;

    private Integer totalRecords;

    private Integer successRecords;

    private Integer failedRecords;

    private LocalDateTime createdAt;
}