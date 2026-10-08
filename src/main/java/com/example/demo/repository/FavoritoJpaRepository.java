package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * FavoritoJpaRepository es la interfaz que define las operaciones de persistencia para la entidad FavoritoEntity.
 * Extiende JpaRepository para obtener las operaciones CRUD por defecto.
 */

@Repository
public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
    List<FavoritoEntity> findByListaId(Long listaId);  
    long countByListaId(Long listaId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
        "UPDATE FavoritoEntity f SET f.lista = :destino WHERE f.lista.id = :origenId")
    void reasignarLista(@org.springframework.data.repository.query.Param("origenId") Long origenId, 
                        @org.springframework.data.repository.query.Param("destino") 
                        com.example.demo.entity.ListaEntity destino);
}
