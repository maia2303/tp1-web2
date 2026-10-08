package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * ListaEntity representa la entidad Lista en la base de datos, esta clase esta mapeada a la tabla
 * "listas" y contiene los campos id y nombre.
 * Se utiliza JPA (Jakarta Persistence API) para la persistencia de datos
 */

@Entity
@Table(name = "listas")
public class ListaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    public ListaEntity(){

    }
    public ListaEntity(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId(){
        return id;
    }
    public String getNombre(){
        return nombre;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
}