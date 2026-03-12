package com.kalyan.ingestion_system.dto;


import lombok.Data;

@Data
public class ProductRowDTO {

    private String name;

    private Double price;

    private Integer quantity;

    private String source;
}