package com.kalyan.ingestion_system.util;

import java.io.InputStream;
import java.security.MessageDigest;

// update repository (FileMetaRepository) & logic in "FIleServiceImpl" (avoid dup)

public class FileHashUtil {

    public static String generateHash(InputStream inputStream) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hash = digest.digest();

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Hash generation failed");
        }
    }
}