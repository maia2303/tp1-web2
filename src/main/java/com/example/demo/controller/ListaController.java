package com.example.demo.controller;

import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.dto.ListaRequestDTO;
import com.example.demo.dto.ListaResponseDTO;
import com.example.demo.services.ListaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas", description = "Endpoints para la gestión de listas de favoritos")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @PostMapping
    @Operation(summary = "Crear lista", description = "Crea una nueva lista de favoritos")
    public ResponseEntity<ListaResponseDTO> crear(@Valid @RequestBody ListaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listaService.crearLista(dto));
    }

    @GetMapping
    @Operation(summary = "Listar listas", description = "Devuelve todas las listas existentes")
    public ResponseEntity<List<ListaResponseDTO>> listar() {
        return ResponseEntity.ok(listaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una lista", description = "Devuelve una lista puntual por su ID")
    public ResponseEntity<ListaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/favoritos")
    @Operation(summary = "Favoritos de una lista", description = "Lista los favoritos que pertenecen a una lista")
    public ResponseEntity<List<FavoritoResponseDTO>> obtenerFavoritos(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.obtenerFavoritosDeLista(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una lista vacía", description = "Elimina una lista si no tiene favoritos asociados")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaService.eliminarLista(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{origenId}/mover-favoritos")
    @Operation(summary = "Mover favoritos entre listas", description = "Reasigna los favoritos a otra lista y elimina el origen de forma atómica")
    public ResponseEntity<Void> moverFavoritos(
            @PathVariable Long origenId,
            @Valid @RequestBody com.example.demo.dto.MoverFavoritosRequestDTO request) {
        listaService.moverFavoritos(origenId, request.destinoId());
        return ResponseEntity.noContent().build();
    }
}