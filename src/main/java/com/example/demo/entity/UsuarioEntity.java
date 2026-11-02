package com.example.demo.entity;

import com.example.demo.model.Rol;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Rol rol;

    public UsuarioEntity() {
    }

    public UsuarioEntity(Long id, String email, String passwordHash, Rol rol) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Long getId() { 
        return id; 
    }
    public String getEmail() { 
        return email; 
    }    
    public String getPasswordHash() { 
        return passwordHash; 
    }    
    public Rol getRol() { 
        return rol; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
    }
    public void setRol(Rol rol) { 
        this.rol = rol; 
    }
}