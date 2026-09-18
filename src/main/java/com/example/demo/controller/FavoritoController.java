package com.example.demo.controller;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.services.FavoritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Operaciones relacionadas a los favoritos de productos")
public class FavoritoController {
    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping
    @Operation(summary = "Agregar favorito", description = "Agrega un producto a la lista de favoritos con una nota opcional")
    public ResponseEntity<FavoritoResponseDTO> crear(@Valid @RequestBody FavoritoRequestDTO favoritoRequestDTO) {
        // Valida el DTO de solicitud y llama al servicio para agregar el favorito
        FavoritoResponseDTO favoritoResponseDTO = favoritoService.agregarFavorito(favoritoRequestDTO);
        
        return new ResponseEntity<>(favoritoResponseDTO, HttpStatus.CREATED); // Devuelve un ResponseEntity con el DTO de respuesta y el código de estado HTTP 201
    } 

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Obtiene una lista de todos los productos marcados como favoritos")
    public ResponseEntity<List<FavoritoResponseDTO>> listarFavoritos() {
        return ResponseEntity.ok(favoritoService.listarFavoritos()); // Devuelve un ResponseEntity con la lista de favoritos y el código de estado HTTP 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener favorito por ID", description = "Obtiene los detalles de un producto favorito específico utilizando su ID")
    public ResponseEntity<FavoritoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(favoritoService.obtenerPorId(id)); // Devuelve un ResponseEntity con el favorito encontrado y el código de estado HTTP 200
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar favorito", description = "Actualiza la nota de un producto favorito específico utilizando su ID")
    public ResponseEntity<FavoritoResponseDTO> actualizarFavorito(@PathVariable Long id, @Valid @RequestBody FavoritoRequestDTO favoritoRequestDTO) {
        return ResponseEntity.ok(favoritoService.actualizarFavorito(id, favoritoRequestDTO)); // Devuelve un ResponseEntity con el DTO de solicitud y el código de estado HTTP 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar favorito", description = "Elimina un producto favorito específico utilizando su ID")
    public ResponseEntity<Void> eliminarFavorito(@PathVariable Long id) {
        favoritoService.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve un ResponseEntity con el código de estado HTTP 204 (No Content) indicando que la eliminación fue exitosa
    }
}
