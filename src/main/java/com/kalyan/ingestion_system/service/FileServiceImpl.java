package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.FileUploadResponseDTO;
import com.kalyan.ingestion_system.dto.ProductRowDTO;
import com.kalyan.ingestion_system.model.FileMetadata;
import com.kalyan.ingestion_system.model.ProcessingAudit;
import com.kalyan.ingestion_system.repository.FileMetadataRepository;
import com.kalyan.ingestion_system.repository.ProcessingAuditRepository;
import com.kalyan.ingestion_system.util.FileHashUtil;
import com.kalyan.ingestion_system.util.FileParser;
import com.kalyan.ingestion_system.util.RowValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final ProcessingAuditRepository processingAuditRepository;
    private final FileMetadataRepository metadataRepository;
    private final ProductService productService;

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile file) {

        // checking emty file
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("Uploaded file is empty");
        }

        String fileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "unknown_file";

        String hash;

        try {
            hash = FileHashUtil.generateHash(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }

        // chekcing duplicate files
        Optional<FileMetadata> existing = metadataRepository.findByFileHash(hash);

        if (existing.isPresent()) {
            throw new IllegalStateException("File already uploaded");
        }

        // save metadata
        FileMetadata metadata = new FileMetadata();

        metadata.setFileHash(hash);
        metadata.setFileName(fileName);
        metadata.setStatus("PENDING");
        metadata.setCreatedAt(LocalDateTime.now());

        metadata = metadataRepository.save(metadata);

        // async processing
        processAsync(file, metadata.getId());

        // showing response
        FileUploadResponseDTO response = new FileUploadResponseDTO();

        response.setFileId(metadata.getId());
        response.setFileName(fileName);
        response.setStatus("PROCESSING");

        return response;
    }

    @Async

    public void processAsync(MultipartFile file, Long fileId) {

        int successCount = 0;
        int failureCount = 0;
        int rowNumber = 1;

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

            reader.readLine(); // skip header

            String line;

            List<ProductRowDTO> batch = new ArrayList<>();

            while ((line = reader.readLine()) != null) {

                rowNumber++;

                try {

                    String[] columns = line.split(",", -1);

                    RowValidator.validate(columns);

                    ProductRowDTO dto = FileParser.parse(line);

                    batch.add(dto);

                    successCount++;

                    if (batch.size() == 100) {
                        productService.saveBatch(batch);
                        batch.clear();
                    }

                } catch (Exception rowError) {

                    failureCount++;

                    ProcessingAudit audit = new ProcessingAudit();

                    audit.setFileId(fileId);
                    audit.setMessage("Row " + rowNumber + " failed: " + rowError.getMessage());
                    audit.setCreatedAt(LocalDateTime.now());

                    processingAuditRepository.save(audit);
                }
            }

            if (!batch.isEmpty()) {
                productService.saveBatch(batch);
            }

            int total = successCount + failureCount;

            FileMetadata metadata = metadataRepository
                    .findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File metadata not found"));

            metadata.setSuccessRecords(successCount);
            metadata.setFailedRecords(failureCount);
            metadata.setTotalRecords(total);

            if (failureCount > 0) {
                metadata.setStatus("FAILED");
            } else {
                metadata.setStatus("SUCCESS");
            }

            metadataRepository.save(metadata);

        } catch (Exception e) {

            FileMetadata metadata = metadataRepository
                    .findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File metadata not found"));

            metadata.setStatus("FAILED");

            metadataRepository.save(metadata);
        }
    }
}