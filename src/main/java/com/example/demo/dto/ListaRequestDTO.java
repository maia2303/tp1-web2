package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListaRequestDTO(
    @NotBlank(message= "El nombre de la lista no puede estar vacio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    String nombre
){}