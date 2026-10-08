package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import com.example.demo.model.Favorito;
import com.example.demo.entity.ListaEntity;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;


/**
 * FavoritoRepositoryAdapter actua como adaptador para la interfaz FavoritoRepository, permitiendo la interaccion con la base de datos a partir de la entidad FavoritoEntity. Implementa los métodos de FavoritoRepository para realizar las operaciones CRUD sobre FavoritoEntity y convertirlas a Favorito.
 */

@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository favoritoJpaRepository;
    private final EntityManager entityManager;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository favoritoJpaRepository, EntityManager entityManager) {
        this.favoritoJpaRepository = favoritoJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Favorito guardar(Favorito favorito) {
        ListaEntity listaRef = null;
        if(favorito.listaId() != null) {
            listaRef = entityManager.getReference(ListaEntity.class, favorito.listaId());
        }
        FavoritoEntity entidad = new FavoritoEntity(
            favorito.id(),
            favorito.productoId(),
            favorito.nota(),
            favorito.fechaAgregado(),
            listaRef
        );
        FavoritoEntity guardada = favoritoJpaRepository.save(entidad);
        return mapearAFavorito(guardada);
    }

    @Override
    public List<Favorito> buscarFavoritos() {
        return favoritoJpaRepository.findAll().stream()
                .map(this::mapearAFavorito)
                .toList();
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return favoritoJpaRepository.findById(id)
                .map(this::mapearAFavorito);
    }

    @Override
    public boolean eliminarPorId(Long id) {
        if (favoritoJpaRepository.existsById(id)) {
            favoritoJpaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Favorito mapearAFavorito(FavoritoEntity e) {
        return new Favorito(
            e.getId(),
            e.getProductoId(),
            e.getNota(),
            e.getFechaAlta(),
            e.getLista() == null ? null : e.getLista().getId()
        );
    }
}