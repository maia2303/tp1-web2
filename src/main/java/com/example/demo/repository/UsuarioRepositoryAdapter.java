package com.example.demo.repository;

import com.example.demo.entity.UsuarioEntity;
import com.example.demo.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

        private Usuario mapearAUsuario(UsuarioEntity e) {
        return new Usuario(
            e.getId(), 
            e.getEmail(), 
            e.getPasswordHash(), 
            e.getRol());
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entidad = new UsuarioEntity(
                usuario.id(),
                usuario.email(),
                usuario.passwordHash(),
                usuario.rol()
        );
        UsuarioEntity guardada = jpaRepository.save(entidad);
        return mapearAUsuario(guardada);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::mapearAUsuario);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

}