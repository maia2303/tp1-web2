package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ProductDTO;
import com.example.demo.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


@RequestMapping("/api/products") //Se define la ruta base para los endpoints
@RestController //Indica que cada método devuelve datos en formato json
@Tag(name = "Products", description = "Operaciones relacionadas al catálogo de productos")
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        //inyección de dependencias del servicio por constructor
        this.productService = productService;
    }

    @GetMapping //esta anotación maneja peticiones HTTP
    @Operation(summary = "Listar productos", description = "Obtiene una lista de todos los productos disponibles en el catálogo") //Anotacion Swagger UI para describir los end´points
    public ResponseEntity<List<ProductDTO>> listarProductos() {
        List<ProductDTO> productos = productService.listarProductos();
        return ResponseEntity.ok(productos); //esta respuesta contiene status, body y header junto con los datos del producto
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico utilizando su ID")
    public ResponseEntity<ProductDTO> obtenerProductoPorId(@PathVariable Long id) {
        //PathVariable toma el valor {id} de la url y lo convierta en una variable id de java
        ProductDTO producto = productService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }
}