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

@Service // Registra la clase como un bean que contiene la lógica de negocio para que pueda ser inyectada en el controlador
public class ProductService {

    private final RestClient restClient; // Cliente HTTP moderno de Spring para consumir APIs REST de forma más fluida

    public ProductService(RestClient restClient) {
        this.restClient = restClient;
    }

    private ProductDTO mapearADto(DummyJsonProducto externo) {
        // Convierte la respuesta externa de DummyJSON en un DTO con solo los campos necesarios
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
            .uri("/products") //Se asigna una URL a la obtención de todos los productos 
            .retrieve() //Se realiza la solicitud HTTP y se recibe la respuesta
            .body(DummyJsonProductosResponse.class); //Toma el dummyJson y lo convierte automáticamente en una instancia de la clase DummyJsonProductosResponse que contiene la lista de productos

            if(respuesta == null || respuesta.products() == null) {
                return List.of();
            }
            return respuesta.products().stream()
                //Si la respuesta contiene productos y no es nula, se mapea cada producto a un ProductDTO
                .map(this::mapearADto)
                .toList();
        } catch (RestClientException e) {
            //Si ocurre un error al comunicarse con el servicio externo, se lanza una excepción personalizada
            throw new ServicioExternoException("Error al comunicarse con el servicio externo DummyJSON", e);
        }
    } 
    
    public ProductDTO obtenerProductoPorId(Long id) {
        try {
            DummyJsonProducto externo = restClient.get()
                .uri("/products/{id}", id) //reemplaza {id} por el parametro recibido
                .retrieve()
                .body(DummyJsonProducto.class); //se espera el producto individual
            
            if(externo == null) {
                throw new RecursoNoEncontradoException("Producto no encontrado con id " + id);
            }
            return mapearADto(externo);
        } catch (HttpClientErrorException.NotFound ex) {
            //dummyJson responde con error 404 cuando el id no existe (error del cliente)
            throw new RecursoNoEncontradoException("Producto no encontrado con ID: " + id);
        } catch (RestClientException e) {
            //excepción cuando falla la conexión o el servidor (error 5xx)
            throw new ServicioExternoException("Error al comunicarse con el servicio externo DummyJSON", e);
        }
    }
    
}