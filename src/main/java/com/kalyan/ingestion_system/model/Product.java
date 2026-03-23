package com.kalyan.ingestion_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double price;

    private Integer quantity;

    private String source;

    // threshold validation
    @Column(name = "file_id")
    private Long fileId;
}