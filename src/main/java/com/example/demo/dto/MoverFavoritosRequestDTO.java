package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record MoverFavoritosRequestDTO(
    @NotNull(message = "El ID de la lista destino es obligatorio")
    Long destinoId
) {}