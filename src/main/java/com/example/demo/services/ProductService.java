package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import com.example.demo.dto.ProductDTO;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import java.util.List;
import com.example.demo.exception.ServicioExternoException;
import com.example.demo.exception.RecursoNoEncontradoException;

@Service
public class ProductService {

    private final RestClient restClient;

    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    private ProductDTO mapearADto(DummyJsonProducto externo) {
        return new ProductDTO(
            externo.id(),
            externo.title(),
            externo.description(),
            externo.price(),
            externo.category()
        );
    }

    public List <ProductDTO> listarProductos(){
        try {
            DummyJsonProductosResponse respuesta = restClient.get()
            .uri("/products")
            .retrieve()
            .body(DummyJsonProductosResponse.class);

            if(respuesta == null || respuesta.products() == null) {
                return List.of();
            }
            return respuesta.products().stream()
                .map(this::mapearADto)
                .toList();
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al comunicarse con el servicio externo DummyJSON", e);
        }
    } 
    
    public ProductDTO obtenerProductoPorId(Long id) {
        try {
            DummyJsonProducto externo = restClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(DummyJsonProducto.class);
            
            if(externo == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con id " + id);
            }
            return mapearADto(externo);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error al comunicarse con el servicio externo DummyJSON", e);
        }
    }
    
}