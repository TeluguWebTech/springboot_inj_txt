
package com.kalyan.ingestion_system.repository;

import com.kalyan.ingestion_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}