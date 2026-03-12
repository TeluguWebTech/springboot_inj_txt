
package com.kalyan.ingestion_system.controller;


import com.kalyan.ingestion_system.dto.FileUploadResponseDTO;
import com.kalyan.ingestion_system.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public FileUploadResponseDTO uploadFile(@RequestParam MultipartFile file) {

        return fileService.uploadFile(file);
    }

}