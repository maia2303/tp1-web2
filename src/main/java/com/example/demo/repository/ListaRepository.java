package com.example.demo.repository;

import com.example.demo.model.Lista;

import java.util.List;
import java.util.Optional;

public interface ListaRepository {
    Lista guardar(Lista lista);

    List<Lista> buscarListas();
    
    List<Lista> buscarPorUsuarioId(Long usuarioId);
    
    Optional<Lista> buscarPorId(Long id);
    
    boolean existePorId(Long id);

    boolean eliminarPorId(Long id);
}
