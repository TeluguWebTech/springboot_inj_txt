package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.FileUploadResponseDTO;
import com.kalyan.ingestion_system.dto.ProductRowDTO;
import com.kalyan.ingestion_system.model.FileMetadata;
import com.kalyan.ingestion_system.repository.FileMetadataRepository;
import com.kalyan.ingestion_system.util.FileParser;
import com.kalyan.ingestion_system.util.FileValidator;
import com.kalyan.ingestion_system.util.RowValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMetadataRepository metadataRepository;
    private final ProductService productService;

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile file) {

        String fileName = file.getOriginalFilename();

        FileValidator.validateFileType(fileName);

        FileMetadata metadata = new FileMetadata();

        metadata.setFileName(fileName);
        metadata.setStatus("PENDING");
        metadata.setCreatedAt(LocalDateTime.now());

        metadata = metadataRepository.save(metadata);

        processAsync(file, metadata.getId());

        FileUploadResponseDTO response = new FileUploadResponseDTO();

        response.setFileId(metadata.getId());
        response.setFileName(fileName);
        response.setStatus("PROCESSING");

        return response;
    }
@Async
public void processAsync(MultipartFile file, Long fileId) {

    int successCount = 0;

    try {

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(file.getInputStream()));

        reader.readLine();

        String line;

        List<ProductRowDTO> batch = new ArrayList<>();

        while ((line = reader.readLine()) != null) {

            String[] columns = line.split(",");

            RowValidator.validate(columns);

            ProductRowDTO dto = FileParser.parse(line);

            batch.add(dto);

            successCount++;

            if (batch.size() == 100) {

                productService.saveBatch(batch);

                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            productService.saveBatch(batch);
        }

        // ⭐ ADD THIS PART

        FileMetadata metadata = metadataRepository.findById(fileId).get();

        metadata.setStatus("SUCCESS");
        metadata.setSuccessRecords(successCount);
        metadata.setTotalRecords(successCount);

        metadataRepository.save(metadata);

    } catch (Exception e) {

        e.printStackTrace();

        FileMetadata metadata = metadataRepository.findById(fileId).get();

        metadata.setStatus("FAILED");

        metadataRepository.save(metadata);
    }
}
}