package com.kalyan.ingestion_system.util;

public class HeaderValidator {

    private static final String EXPECTED_HEADER = "name,price,quantity,source";

    public static void validate(String headerLine) {

        if (headerLine == null || headerLine.trim().isEmpty()) {
            throw new RuntimeException("Header is missing");
        }

        if (!EXPECTED_HEADER.equalsIgnoreCase(headerLine.trim())) {
            throw new RuntimeException(
                    "Invalid header. Expected: " + EXPECTED_HEADER + " but found: " + headerLine
            );
        }
    }
}