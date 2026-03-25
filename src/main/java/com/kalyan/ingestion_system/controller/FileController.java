package com.kalyan.ingestion_system.controller;

import com.kalyan.ingestion_system.model.FileMetadata;
import com.kalyan.ingestion_system.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileMetadataRepository repository;

    //  GET ALL FILES
    @GetMapping
    public List<FileMetadata> getAllFiles() {
        return repository.findAll();
    }

    //  GET BY ID
    @GetMapping("/{id}")
    public FileMetadata getFile(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }
}