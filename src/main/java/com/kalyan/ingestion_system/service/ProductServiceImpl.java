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

    @Override
    public void saveBatch(List<ProductRowDTO> rows) {

        List<Product> products = new ArrayList<>();

        for (ProductRowDTO dto : rows) {

            Product product = new Product();

            product.setName(dto.getName());
            product.setPrice(dto.getPrice());
            product.setQuantity(dto.getQuantity());
            product.setSource(dto.getSource());

            products.add(product);
        }

        productRepository.saveAll(products);
    }
}