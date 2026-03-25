package com.kalyan.ingestion_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileEventDTO {

    private Long fileId;
    private int success;
    private int failed;
    private String status;
}