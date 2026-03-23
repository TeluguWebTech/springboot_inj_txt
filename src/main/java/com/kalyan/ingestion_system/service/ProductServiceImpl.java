package com.kalyan.ingestion_system.service;

import com.kalyan.ingestion_system.dto.ProductRowDTO;
import com.kalyan.ingestion_system.model.Product;
import com.kalyan.ingestion_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // @Override
    // public void saveBatch(List<ProductRowDTO> rows) {

    // List<Product> products = new ArrayList<>();

    // for (ProductRowDTO dto : rows) {

    // Product product = new Product();

    // product.setName(dto.getName());
    // product.setPrice(dto.getPrice());
    // product.setQuantity(dto.getQuantity());
    // product.setSource(dto.getSource());

    // products.add(product);
    // }

    // productRepository.saveAll(products);
    // }
    @Override
    public void saveBatch(List<ProductRowDTO> batch, Long fileId) {

        List<Product> products = batch.stream().map(dto -> {
            Product p = new Product();
            p.setName(dto.getName());
            p.setPrice(dto.getPrice());
            p.setQuantity(dto.getQuantity());
            p.setSource(dto.getSource());

            p.setFileId(fileId);

            return p;
        }).toList();

        productRepository.saveAll(products);
    }

    @Override
    public void deleteByFileId(Long fileId) {
        productRepository.deleteByFileId(fileId);
    }
}