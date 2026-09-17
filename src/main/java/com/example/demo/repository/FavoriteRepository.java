package com.example.demo.repository;

import com.example.demo.domain.Favorito;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository {

    Favorito guardar(Favorito favorito);

    List<Favorito> buscarFavoritos();

    Optional<Favorito> buscarPorId(Long id);

    boolean eliminarPorId(Long id);

    boolean existePorId(Long id);

}