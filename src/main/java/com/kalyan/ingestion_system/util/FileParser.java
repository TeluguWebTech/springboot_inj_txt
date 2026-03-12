package com.kalyan.ingestion_system.util;

import com.kalyan.ingestion_system.dto.ProductRowDTO;

public class FileParser {

    public static ProductRowDTO parse(String line) {

        String[] columns = line.split(",");

        ProductRowDTO dto = new ProductRowDTO();

        dto.setName(columns[0]);
        dto.setPrice(Double.parseDouble(columns[1]));
        dto.setQuantity(Integer.parseInt(columns[2]));
        dto.setSource(columns[3]);

        return dto;
    }

}