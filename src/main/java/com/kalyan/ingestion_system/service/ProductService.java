package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.ProductRowDTO;

import java.util.List;

public interface ProductService {

    // void saveBatch(List<ProductRowDTO> rows);

    // threshold validation
    void saveBatch(List<ProductRowDTO> batch, Long fileId);

    void deleteByFileId(Long fileId);

}