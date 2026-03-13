package com.kalyan.ingestion_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "file_metadata")
public class FileMetadata {

     // added new field to avoid duplication (fileHash), next...
    // next creating Hash in utility (util/FileHashUtil)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_hash", unique = true)
    private String fileHash;

    @Column(name = "file_name")
    private String fileName;

    private String status;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_records")
    private Integer successRecords;

    @Column(name = "failed_records")
    private Integer failedRecords;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}