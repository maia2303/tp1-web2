package com.example.demo.repository;

import com.example.demo.model.Favorito;
import java.util.List;
import java.util.Optional;


//Interface para declarar qué puede hacer el repositorio pero no como puede hacerlo
public interface FavoriteRepository {

    Favorito guardar(Favorito favorito); 

    List<Favorito> buscarFavoritos();

    Optional<Favorito> buscarPorId(Long id);

    boolean eliminarPorId(Long id);

}