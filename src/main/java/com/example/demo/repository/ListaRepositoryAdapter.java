package com.example.demo.repository;

import com.example.demo.entity.ListaEntity;
import com.example.demo.model.Lista;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class ListaRepositoryAdapter implements ListaRepository {
    private final ListaJpaRepository listaJpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository listaJpaRepository){
        this.listaJpaRepository = listaJpaRepository;
    }

    @Override
    public Lista guardar(Lista lista){
        ListaEntity entidad = new ListaEntity(lista.id(), lista.nombre());
        ListaEntity guardada = listaJpaRepository.save(entidad);
        return new Lista(guardada.getId(), guardada.getNombre());
    }

    @Override
    public List<Lista> buscarListas(){
        return listaJpaRepository.findAll().stream()
            .map(e -> new Lista(e.getId(), e.getNombre()))
            .toList();
    }

    @Override
    public Optional<Lista> buscarPorId(Long id){
        return listaJpaRepository.findById(id)
            .map(e -> new Lista(e.getId(), e.getNombre()));
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
}
