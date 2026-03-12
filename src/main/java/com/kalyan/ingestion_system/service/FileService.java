package com.kalyan.ingestion_system.service;


import com.kalyan.ingestion_system.dto.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadResponseDTO uploadFile(MultipartFile file);

}