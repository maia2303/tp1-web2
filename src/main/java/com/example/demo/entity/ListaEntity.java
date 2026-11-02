package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * ListaEntity representa la entidad Lista en la base de datos, esta clase esta mapeada a la tabla
 * "listas" y contiene los campos id y nombre.
 * Se utiliza JPA (Jakarta Persistence API) para la persistencia de datos
 */

@Entity
@Table(name = "listas")
public class ListaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    public ListaEntity() {}

    public ListaEntity(Long id, String nombre, UsuarioEntity usuario) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public UsuarioEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioEntity usuario) { this.usuario = usuario; }
}