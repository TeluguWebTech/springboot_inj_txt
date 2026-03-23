package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.FileUploadResponseDTO;
import com.kalyan.ingestion_system.dto.ProductRowDTO;
import com.kalyan.ingestion_system.model.FileMetadata;
import com.kalyan.ingestion_system.model.ProcessingAudit;
import com.kalyan.ingestion_system.repository.FileMetadataRepository;
import com.kalyan.ingestion_system.repository.ProcessingAuditRepository;
import com.kalyan.ingestion_system.util.FileHashUtil;
import com.kalyan.ingestion_system.util.FileParser;
import com.kalyan.ingestion_system.util.HeaderValidator;
import com.kalyan.ingestion_system.util.RowValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${file.processing.failure-threshold}")
    private int failureThreshold;

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile file) {

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
        int trailerCount = -1;

        boolean isFailed = false;

        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream()));

            // reader.readLine(); // skip header
            // point to remember, what if no header?
            // newly added (header)
            String header = reader.readLine();
            HeaderValidator.validate(header);

            // checking column count
            String[] headerColumns = header.split(",");
            if (headerColumns.length != 4) {
                throw new RuntimeException("Invalid header column count");
            }

            String line;
            List<ProductRowDTO> batch = new ArrayList<>();

            // processing files
            while ((line = reader.readLine()) != null) {

                // TRAILER DETECTION
                if (line.startsWith("TRAILER|")) {

                    String[] parts = line.split("\\|");

                    if (parts.length != 2) {
                        throw new RuntimeException("Invalid trailer format");
                    }

                    try {
                        trailerCount = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid trailer count");
                    }

                    break;
                }

                rowNumber++;

                try {

                    String[] columns = line.split(",", -1);

                    RowValidator.validate(columns);

                    ProductRowDTO dto = FileParser.parse(line);

                    batch.add(dto);

                    successCount++;

                    if (batch.size() == 100) {
                        productService.saveBatch(batch, fileId);
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

            // save remaining batch
            if (!batch.isEmpty()) {
                // productService.saveBatch(batch);
                productService.saveBatch(batch, fileId);
            }

            int total = successCount + failureCount;

            // TRAILER VALIDATION
            if (trailerCount == -1) {
                throw new RuntimeException("Trailer missing");
            }

            if (total != trailerCount) {
                throw new RuntimeException(
                        "Trailer count mismatch. Expected: " + trailerCount + " but found: " + total);
            }

            // UPDATE METADATA
            FileMetadata metadata = metadataRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File metadata not found"));

            metadata.setSuccessRecords(successCount);
            metadata.setFailedRecords(failureCount);
            metadata.setTotalRecords(total);

            // if (failureCount > 0) {
            // metadata.setStatus("FAILED");
            // } else {
            // metadata.setStatus("SUCCESS");
            // }
            // Threshold (with dynamic count)
            if (failureCount == 0) {
                metadata.setStatus("SUCCESS");

            } else if (failureCount <= failureThreshold) {
                metadata.setStatus("FAILED");
                isFailed = true;

            } else {
                metadata.setStatus("FAILED");
                isFailed = true;

                ProcessingAudit audit = new ProcessingAudit();
                audit.setFileId(fileId);
                audit.setMessage("HIGH ERROR COUNT: Threshold exceeded (" + failureCount + ")");
                audit.setCreatedAt(LocalDateTime.now());

                processingAuditRepository.save(audit);
            }

            metadataRepository.save(metadata);

        } catch (Exception e) {
            isFailed = true;
            // checking error & updating to audit_processing
            ProcessingAudit audit = new ProcessingAudit();

            audit.setFileId(fileId);
            audit.setMessage("File validation failed: " + e.getMessage());
            audit.setCreatedAt(LocalDateTime.now());

            processingAuditRepository.save(audit);

            // update file status
            FileMetadata metadata = metadataRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File metadata not found"));

            metadata.setStatus("FAILED");
            metadata.setSuccessRecords(0);
            metadata.setFailedRecords(0);
            metadata.setTotalRecords(0);

            metadataRepository.save(metadata);
        }
        if (isFailed) {

            productService.deleteByFileId(fileId);

            ProcessingAudit audit = new ProcessingAudit();
            audit.setFileId(fileId);
            audit.setMessage("Rollback performed: All records deleted");
            audit.setCreatedAt(LocalDateTime.now());

            processingAuditRepository.save(audit);
        }
    }
}