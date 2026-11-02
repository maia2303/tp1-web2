package com.example.demo.repository;

import com.example.demo.entity.ListaEntity;
import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.model.Lista;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class ListaRepositoryAdapter implements ListaRepository {
    private final ListaJpaRepository listaJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public ListaRepositoryAdapter(
            ListaJpaRepository listaJpaRepository,
            UsuarioJpaRepository usuarioJpaRepository) {
        this.listaJpaRepository = listaJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Lista guardar(Lista lista) {
        var usuario = lista.usuarioId() == null
                ? null
                : usuarioJpaRepository.findById(lista.usuarioId())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No se encontró el usuario con ID: " + lista.usuarioId()));
        ListaEntity entidad = new ListaEntity(lista.id(), lista.nombre(), usuario);
        ListaEntity guardada = listaJpaRepository.save(entidad);
        return mapearALista(guardada);
    }

    @Override
    public List<Lista> buscarListas() {
        return listaJpaRepository.findAll().stream()
            .map(this::mapearALista)
            .toList();
    }

    @Override
    public List<Lista> buscarPorUsuarioId(Long usuarioId) {
        return listaJpaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearALista)
                .toList();
    }

    @Override
    public Optional<Lista> buscarPorId(Long id) {
        return listaJpaRepository.findById(id)
                .map(this::mapearALista);
    }

    @Override
    public boolean existePorId(Long id) {
        return listaJpaRepository.existsById(id);
    }

    @Override
    public boolean eliminarPorId(Long id){
        if(listaJpaRepository.existsById(id)){
            listaJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<ListaEntity> buscarEntidadPorId(Long id) {
        return listaJpaRepository.findById(id);
    }

    private Lista mapearALista(ListaEntity entidad) {
        Long usuarioId = entidad.getUsuario() == null ? null : entidad.getUsuario().getId();
        return new Lista(entidad.getId(), entidad.getNombre(), usuarioId);
    }
}
