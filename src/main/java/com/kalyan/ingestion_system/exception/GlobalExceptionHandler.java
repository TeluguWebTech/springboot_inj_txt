package com.kalyan.ingestion_system.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<String> handleFileValidation(FileValidationException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}