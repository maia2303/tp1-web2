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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    //metodo auxiliar para obtener el id del usuario autenticado desde el contexto de seguridad
    private Long getUsuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getDetails(); // lo guardamos aca desde el JwtAuthenticationFilter
    }

    private boolean isAdministrador() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validarPropiedad(Lista lista) {
        if (!isAdministrador() && !lista.usuarioId().equals(getUsuarioAutenticadoId())) {
            // Spring Security traduce automaticamente esta excepción a un 403 
            throw new AccessDeniedException("No tienes permiso para acceder a esta lista");
        }
    }

    public ListaResponseDTO crearLista(ListaRequestDTO dto) {
        Long usuarioId = getUsuarioAutenticadoId();
        // al crear, el dueño es el usuario autenticado 
        Lista nueva = new Lista(null, dto.nombre(), usuarioId);
        Lista guardada = listaRepository.guardar(nueva);
        return new ListaResponseDTO(guardada.id(), guardada.nombre());
    }

    public List<ListaResponseDTO> listarTodas() {
        // devuelve las del usuario, o todas si es ADMIN
        List<Lista> listas = isAdministrador() 
                ? listaRepository.buscarListas() 
                : listaRepository.buscarPorUsuarioId(getUsuarioAutenticadoId());

        return listas.stream()
                .map(l -> new ListaResponseDTO(l.id(), l.nombre()))
                .toList();
    }

    public ListaResponseDTO obtenerPorId(Long id) {
        Lista lista = listaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista con ID: " + id));
        
        validarPropiedad(lista); // valida dueño o admin (403 Forbidden)
        
        return new ListaResponseDTO(lista.id(), lista.nombre());
    }

    public List<FavoritoResponseDTO> obtenerFavoritosDeLista(Long listaId) {
        Lista lista = listaRepository.buscarPorId(listaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista con ID: " + listaId));
        
        validarPropiedad(lista);
        
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
        Lista lista = listaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro la lista con ID: " + id));
        
        validarPropiedad(lista);
        
        if (favoritoJpaRepository.countByListaId(id) > 0) {
            throw new ListaNoVaciaException("No se puede eliminar la lista porque contiene favoritos asociados");
        }
        listaRepository.eliminarPorId(id);
    }
    
    @org.springframework.transaction.annotation.Transactional
    public void moverFavoritos(Long origenId, Long destinoId) {
        Lista origen = listaRepository.buscarPorId(origenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista origen con ID: " + origenId));
        validarPropiedad(origen);

        Lista destino = listaRepository.buscarPorId(destinoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la lista destino con ID: " + destinoId));
        validarPropiedad(destino);

        ListaEntity destinoEntity = listaJpaRepository.findById(destinoId).get();

        favoritoJpaRepository.reasignarLista(origenId, destinoEntity);
        listaRepository.eliminarPorId(origenId);
    }
}