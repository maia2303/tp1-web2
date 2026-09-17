package com.example.demo.repository;

import com.example.demo.repository.FavoriteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class MemoryFavRepository implements FavoriteRepository {
    private final Map<Long, Favorito> tablaFavorito = new ConcurrentHashMap<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    @Override
    
}