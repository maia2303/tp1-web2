package com.example.demo.dto;

//DTO (Data Transfer Object) para representar un producto y no usar todos los datos del dummyJson para la api
public record ProductDTO(
    Long id,
    String title,
    String description,
    double price,
    String category
    //uso los mismos tipos que en dummyJson para evitar conflictos
){}