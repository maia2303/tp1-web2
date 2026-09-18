package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FavoritoRequestDTO(
    @NotNull(message = "productoId es obligatorio")
    @Positive(message = "productoId debe ser un número positivo")
    Long productoId,
    @NotBlank(message = "nota no puede estar vacio")
    String nota
) { }
