package com.example.demo.dto;


public record ProductDTO(
    Long id,
    String title,
    String description,
    double price,
    String category
){}