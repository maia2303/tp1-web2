# API de Favoritos - Trabajo Práctico 1

Esta es una API RESTful desarrollada con Spring Boot para la gestión de Favoritos. El proyecto implementa una arquitectura por capas (Controller, Service, Repository), validación de datos de entrada y consume la API externa de DummyJSON para verificar la existencia de los productos antes de guardarlos.

## Instrucciones para levantar el proyecto

1. Clonar este repositorio.
2. Abrir el proyecto en tu IDE (como Visual Studio Code o IntelliJ).
3. Asegurarte de tener instalado Java 17 o superior.
4. Ejecutar la clase principal `DemoApplication.java` (o correr el comando `./mvnw spring-boot:run` en la terminal).
5. El servidor se iniciará en el puerto `8080`.

## Documentación (Swagger UI)

La API está completamente documentada con OpenAPI. Una vez que el proyecto esté corriendo, podés explorar y probar todos los endpoints desde la interfaz gráfica de Swagger ingresando a:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

## Evidencia de Pruebas

En la carpeta `/evidencia` de este repositorio se encuentran las capturas de pantalla o colecciones que demuestran el correcto funcionamiento de los casos de éxito y de error para los recursos solicitados.