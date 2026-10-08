package com.example.demo.services;

import com.example.demo.dto.FavoritoResponseDTO;
import com.example.demo.dto.ListaRequestDTO;
import com.example.demo.dto.ListaResponseDTO;
import com.example.demo.entity.ListaEntity;
import com.example.demo.exception.ListaNoVaciaException;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoJpaRepository;
import com.example.demo.repository.ListaJpaRepository;
import com.example.demo.repository.ListaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaService {
    private final ListaRepository listaRepository;
    private final FavoritoJpaRepository favoritoJpaRepository;
    private final ListaJpaRepository listaJpaRepository;

    public ListaService(
            ListaRepository listaRepository,
            FavoritoJpaRepository favoritoJpaRepository,
            ListaJpaRepository listaJpaRepository) {
        this.listaRepository = listaRepository;
        this.favoritoJpaRepository = favoritoJpaRepository;
        this.listaJpaRepository = listaJpaRepository;
    }
    public ListaResponseDTO crearLista(ListaRequestDTO dto) {
        Lista nueva = new Lista(null, dto.nombre());
        Lista guardada = listaRepository.guardar(nueva);
        return new ListaResponseDTO(guardada.id(), guardada.nombre());
    }

    public List<ListaResponseDTO> listarTodas() {
        return listaRepository.buscarListas().stream()
                .map(l -> new ListaResponseDTO(l.id(), l.nombre()))
                .toList();
    }

    public ListaResponseDTO obtenerPorId(Long id) {
        Lista lista = listaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista con ID: " + id));
        return new ListaResponseDTO(lista.id(), lista.nombre());
    }

    public List<FavoritoResponseDTO> obtenerFavoritosDeLista(Long listaId) {
        if (!listaRepository.existePorId(listaId)) {
            throw new RecursoNoEncontradoException("No se encontró la lista con ID: " + listaId);
        }
        return favoritoJpaRepository.findByListaId(listaId).stream()
                .map(e -> new FavoritoResponseDTO(
                        e.getId(),
                        e.getProductoId(),
                        e.getNota(),
                        e.getFechaAlta(),
                        listaId
                ))
                .toList();
    }

    public void eliminarLista(Long id) {
        if (!listaRepository.existePorId(id)) {
            throw new RecursoNoEncontradoException("No se encontro la lista con ID: " + id);
        }
        if (favoritoJpaRepository.countByListaId(id) > 0) {
            throw new ListaNoVaciaException("No se puede eliminar la lista porque contiene favoritos asociados");
        }
        listaRepository.eliminarPorId(id);
    }
    
    @org.springframework.transaction.annotation.Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        if (!listaRepository.existePorId(origenId)) {
            throw new RecursoNoEncontradoException("No se encontró la lista origen con ID: " + origenId);
        }
        ListaEntity destinoEntity = listaJpaRepository.findById(destinoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista destino con ID: " + destinoId));

        favoritoJpaRepository.reasignarLista(origenId, destinoEntity);
        listaRepository.eliminarPorId(origenId);
    }
}