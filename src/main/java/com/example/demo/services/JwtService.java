package com.example.demo.services;

import com.example.demo.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        long expiracionEnMilisegundos = 1000 * 60 * 60 * 24; // 1 día

        return Jwts.builder()
                .subject(usuario.email())
                .claim("id", usuario.id())
                .claim("rol", usuario.rol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracionEnMilisegundos))
                .signWith(secretKey)
                .compact();
    }

    public io.jsonwebtoken.Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}