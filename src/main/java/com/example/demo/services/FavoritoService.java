package com.example.demo.services;

import com.example.demo.dto.FavoritoRequestDTO;
import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Favorito;
import com.example.demo.repository.FavoritoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;


@Service
public class FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final ProductService productService;

    private FavoritoResponseDTO mapearAResponse(Favorito fav) {
        return new FavoritoResponseDTO(
            fav.id(),
            fav.productoId(),
            fav.nota(),
            fav.fechaAgregado()
        );
    }
        
    public FavoritoService(FavoritoRepository favoritoRepository, ProductService productService){
        this.favoritoRepository = favoritoRepository;
        this.productService = productService;
    }
    public FavoritoResponseDTO agregarFavorito(FavoritoRequestDTO favoritoRequestDTO) {
        // Verifica si el producto existe
        productService.obtenerProductoPorId(favoritoRequestDTO.productoId());
        // Crea un nuevo objeto Favorito con los datos del DTO y la fecha actual
        Favorito favorito = new Favorito(
                null,
                favoritoRequestDTO.productoId(),
                favoritoRequestDTO.nota(),
                LocalDateTime.now()
        );

        Favorito favoritoGuardado = favoritoRepository.guardar(favorito); //guarda el favorito en el repositorio
        return mapearAResponse(favoritoGuardado);// devuelve un DTO de respuesta con los datos del favorito guardado
    }

    public List<FavoritoResponseDTO> listarFavoritos(){
        //devuelve una lista de DTOs de favoritos
        return favoritoRepository.buscarFavoritos().stream()
            .map(this::mapearAResponse) //mapea cada favorito a un DTO de respuesta
            .toList();
    }

    public FavoritoResponseDTO obtenerPorId(Long id) {
        //busca el favorito por id y si no lo encuentra lanza una excepción
        Favorito favorito = favoritoRepository.buscarPorId(id) 
            .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID " + id));

        return mapearAResponse(favorito); //si lo encuentra devuelve el favorito
    }

    public FavoritoResponseDTO actualizarFavorito(Long id, FavoritoRequestDTO request) {
        //verificar que existe
        Favorito existente = favoritoRepository.buscarPorId(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID " + id));

        //si cambio el productoId, verificar que el nuevo exista

        if(!existente.productoId().equals(request.productoId())) {
            productService.obtenerProductoPorId(request.productoId());
        }

        Favorito actualizado = new Favorito(
            id,
            request.productoId(),
            request.nota(),
            existente.fechaAgregado() //se mantiene la fecha de agregado original
        );

        Favorito favoritoGuardado = favoritoRepository.guardar(actualizado);
        return mapearAResponse(favoritoGuardado);
    }

    public void eliminar(Long id){
        boolean eliminado = favoritoRepository.eliminarPorId(id);
        if(!eliminado) {
            throw new RecursoNoEncontradoException("Favorito con id " + id + " no encontrado");
        }
    }


}
