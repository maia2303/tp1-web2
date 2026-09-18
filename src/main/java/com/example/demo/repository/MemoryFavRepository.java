package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository //le dice a spring que esta clase va a usar datos
public class MemoryFavRepository implements FavoritoRepository {

    private final Map<Long, Favorito> tablaFavorito = new ConcurrentHashMap<>(); //Map almacena pares clave(Long)-valor(Favorito) y ConcurrentHashMap garantiza que si dos o más hilos consultan, se bloquea una única celda (la que se esta modificando) y no toda la tabla
    private final AtomicLong contadorId = new AtomicLong(1); //incrementa el valor de manera atómica garantizando que cada llamado reciba un número único consecutivo

    @Override
    public Favorito guardar(Favorito favorito) {
        Favorito favoritoAGuardar = favorito;

        if(favorito.id() == null) {
            //como es inmutable, instanciamos un nuevo record con el ID generado
            favoritoAGuardar = new Favorito(
                contadorId.getAndIncrement(), 
                favorito.productoId(), 
                favorito.nota(), 
                favorito.fechaAgregado()
            );
        }
        tablaFavorito.put(favoritoAGuardar.id(), favoritoAGuardar); //actualiza el id y el valor del favorito en la tabla
        return favoritoAGuardar;
    }

    @Override
    public List<Favorito> buscarFavoritos() {
        return new ArrayList<>(tablaFavorito.values()); //devuelve una lista de todos los valores de la tabla
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return Optional.ofNullable(tablaFavorito.get(id)); //devuelve un Optional que puede contener un Favorito o estar vacío si no se encuentra
    }

    @Override
    public boolean eliminarPorId(Long id) {
        return tablaFavorito.remove(id) != null; //elimina el favorito por id y devuelve true si se eliminó, false si no existía
    }

}