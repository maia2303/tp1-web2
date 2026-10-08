package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * FavoritoEntity representa la entidad Favorito en la base de datos, esta clase esta mapeada a la tabla
 * "favoritos" y contiene los campos id, productoId, nota y fechaAlta.
 * Se utiliza JPA (Jakarta Persistence API) para la persistencia de datos
 */

@Entity
@Table(name = "favoritos")
public class FavoritoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nota", length = 250, nullable = false)
    private String nota;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id")
    private ListaEntity lista;

    public FavoritoEntity(){}

    public FavoritoEntity(Long id, Long productoId, String nota, LocalDateTime fechaAlta, ListaEntity lista){
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAlta = fechaAlta;
        this.lista = lista;
    }

    public Long getId(){
        return id;
    }
    public Long getProductoId(){
        return productoId;
    }
    public String getNota(){
        return nota;
    }
    public LocalDateTime getFechaAlta(){
        return fechaAlta;
    }
    public ListaEntity getLista(){
        return lista;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public void setProductoId(Long productoId){
        this.productoId = productoId;
    }
    public void setNota(String nota){
        this.nota = nota;
    }
    public void setFechaAlta(LocalDateTime fechaAlta){
        this.fechaAlta = fechaAlta;
    }
    public void setLista(ListaEntity lista){
        this.lista = lista;
    }
}