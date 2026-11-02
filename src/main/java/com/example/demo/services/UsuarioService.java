package com.example.demo.services;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegistroRequestDTO;
import com.example.demo.dto.TokenResponseDTO;
import com.example.demo.exception.EmailYaRegistradoException;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registrarUsuario(RegistroRequestDTO dto) {
        if (usuarioRepository.existePorEmail(dto.email())) {
            throw new EmailYaRegistradoException("El email " + dto.email() + " ya está registrado.");
        }
        String passwordHasheada = passwordEncoder.encode(dto.password());
        Usuario nuevoUsuario = new Usuario(null, dto.email(), passwordHasheada, Rol.USER);
        usuarioRepository.guardar(nuevoUsuario);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.buscarPorEmail(dto.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.password(), usuario.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);
        return new TokenResponseDTO(token);
    }
}