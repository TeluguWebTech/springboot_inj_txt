package com.kalyan.ingestion_system.dto;


import lombok.Data;

@Data
public class FileUploadResponseDTO {

    private Long fileId;

    private String fileName;

    private String status;
}