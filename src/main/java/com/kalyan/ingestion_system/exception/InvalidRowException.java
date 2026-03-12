package com.kalyan.ingestion_system.exception;


public class InvalidRowException extends RuntimeException {

    public InvalidRowException(String message) {
        super(message);
    }
}