package com.example.demo.repository;

import com.example.demo.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
}