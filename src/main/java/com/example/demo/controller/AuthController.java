package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.RegistroRequestDTO;
import com.example.demo.dto.TokenResponseDTO;
import com.example.demo.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints públicos para registro e inicio de sesión de usuarios (no requieren token JWT)")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario con rol USER por defecto y contraseña hasheada con BCrypt. Devuelve 201 Created o 409 Conflict si el email ya está registrado."
    )
    public ResponseEntity<String> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        usuarioService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito.");
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Valida las credenciales (email y contraseña) y devuelve un token JWT firmado con el id y el rol del usuario. Devuelve 200 OK con el token o 401 Unauthorized si las credenciales son inválidas."
    )
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}