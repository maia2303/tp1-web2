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
@Tag(name = "Autenticación", description = "Endpoints para registro y acceso de usuarios")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario con rol USER. La contraseña se encripta.")
    public ResponseEntity<String> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        usuarioService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito.");
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token JWT")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}