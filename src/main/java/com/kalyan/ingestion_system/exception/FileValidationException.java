package com.kalyan.ingestion_system.exception;


public class FileValidationException extends RuntimeException {

    public FileValidationException(String message) {
        super(message);
    }
}