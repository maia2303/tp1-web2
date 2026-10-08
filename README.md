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

# Puertos y adaptadores: Migraciones de memoria con JPA - Trabajo Práctico 2

## Clases modificadas vs Clases no modificadas

* **Clases que NO cambiaron:**
  * `FavoritoController`: Sigue atendiendo las peticiones HTTP y delegando en la interfaz de servicio sin conocer dónde se guardan los datos.
  * `FavoritoService`: Sigue ejecutando la lógica de negocio y las validaciones dependiendo exclusivamente del contrato `FavoritoRepository`.
  * `Favorito` (modelo de dominio / record): Mantiene su inmutabilidad y representación de negocio independiente de la infraestructura de persistencia.
  * `FavoritoRequestDTO` y `FavoritoResponseDTO`: Mantienen la estructura del contrato de entrada y salida de la API.

* **Clases que cambiaron o se agregaron:**
  * `FavoritoEntity` (nueva): Clase anotada con `@Entity` para que Hibernate gestione el ciclo de vida, la tabla relacional y el estado mutable requerido por JPA.
  * `FavoritoJpaRepository` (nueva): Interfaz de infraestructura que extiende `JpaRepository<FavoritoEntity, Long>` de Spring Data.
  * `FavoritoRepositoryAdapter` (nueva): Implementación concreta que cumple el contrato del puerto `FavoritoRepository`, mapeando entre el record de dominio `Favorito` y `FavoritoEntity`.
  * `MemoryFavRepository` (eliminada): Se removió la implementación en memoria para que Spring inyecte el nuevo adaptador JPA sin ambigüedad.

### Justificacion

Esta migración fue posible gracias al principio de inversión de dependencias y la arquitectura hexagonal:
`FavoritoRepository` actúa como un **puerto** (un contrato abstracto que define que operaciones necesita el dominio). 
Tanto la implementación en memoria (`MemoryFavRepository`) como la de base de datos relacional (`FavoritoRepositoryAdapter`) son **adapters** intercambiables. La lógica de negocio (`FavoritoService`) depende únicamente del puerto, por lo que cambiar el soporte de almacenamiento de un `Map` a PostgreSQL mediante JPA/Hibernate no requiere modificar ninguna línea de código del dominio, el servicio ni los controladores.


## consigna 8:
### Evolución del esquema con Flyway (Migración V4)
No se modifican migraciones anteriores (`V1`, `V2`, `V3`) porque ya fueron aplicadas en la base de datos y registradas con un hash/checksum en `flyway_schema_history`. Modificar un archivo pasado rompe la integridad de versiones de Flyway e invalida despliegues productivos. Los cambios incrementales siempre deben realizarse mediante una migración nueva hacia adelante.

### Transaccionalidad y Atomicidad (ACID)
La operación `mover-favoritos` requiere `@Transactional` para garantizar la **Atomicidad** (la 'A' de ACID). Si fallara la eliminación de la lista origen después de haber reasignado los favoritos, sin `@Transactional` la base quedaría en un estado inconsistente parcial (favoritos mudados pero el origen sin borrar). Con `@Transactional`, si cualquier sentencia falla, se ejecuta un rollback automático devolviendo la base a su estado original.