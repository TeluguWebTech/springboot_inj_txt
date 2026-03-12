package com.kalyan.ingestion_system.util;


import com.kalyan.ingestion_system.exception.FileValidationException;

public class FileValidator {

    public static void validateFileType(String fileName) {

        if (!fileName.endsWith(".txt") && !fileName.endsWith(".csv")) {
            throw new FileValidationException("Invalid file type");
        }

    }

}