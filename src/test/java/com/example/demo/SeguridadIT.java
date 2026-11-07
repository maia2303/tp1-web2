package com.example.demo;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.model.Lista;
import com.example.demo.model.Rol;
import com.example.demo.repository.ListaRepository;
import com.example.demo.repository.UsuarioJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SeguridadIT {
    
    @Container // define un contenedor de PostgreSQL para las pruebas de integración
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3");

    @DynamicPropertySource // permite configurar propiedades dinámicas para la prueba
    static void propiedadesDeConecion(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc; // para simular peticiones HTTP a los endpoints

    private Long listaUser1Id;

    private final ObjectMapper objectMapper = new ObjectMapper(); // para convertir objetos a JSON y viceversa 

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository; // para interactuar con la base de datos de usuarios

    @Autowired
    private ListaRepository listaRepository; // para interactuar con la base de datos de listas

    @Autowired
    private PasswordEncoder passwordEncoder; // para codificar contraseñas

    @BeforeEach //beforeEach se ejecuta antes de cada prueba para limpiar la base de datos y crear datos iniciales
    void setUp() { // metodo para preparar el entorno de prueba antes de cada test
        listaRepository.buscarListas().forEach(l -> listaRepository.eliminarPorId(l.id()));
        usuarioJpaRepository.deleteAll();

        UsuarioEntity user1 = usuarioJpaRepository.save(new UsuarioEntity(null, "ana@test.com", passwordEncoder.encode("123456"), Rol.USER));
        usuarioJpaRepository.save(new UsuarioEntity(null, "admin@test.com", passwordEncoder.encode("123456"), Rol.ADMIN));

        Lista lista = listaRepository.guardar(new Lista(null, "Lista de Ana", user1.getId()));
        listaUser1Id = lista.id();
    }

    private String registrarYLoguear(String email, String password) throws Exception {
        usuarioJpaRepository.save(new UsuarioEntity(null, email, passwordEncoder.encode(password), Rol.USER));
        LoginRequestDTO login = new LoginRequestDTO(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login") 
        // realiza una petición POST al endpoint de login con el email y la contraseña proporcionados
                        .contentType(MediaType.APPLICATION_JSON) // indica que el contenido de la petición es JSON
                        .content(objectMapper.writeValueAsString(login))) // convierte el objeto login a JSON y lo agrega al cuerpo de la petición
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void login_conCredencialesValidas_devuelve200ConToken() throws Exception {
        // metodo para probar que el login con credenciales válidas devuelve 200 OK y un token JWT
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("ana@test.com", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_conCredencialesInvalidas_devuelve401() throws Exception { 
        //metodo para probar que el login con credenciales inválidas devuelve un estado 401 (no autorizado)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("ana@test.com", "malpassword"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceder_aListaPropia_devuelve200() throws Exception {
        // metodo para probar que un usuario puede acceder a su propia lista y recibe un estado 200 (OK)
        String token = registrarYLoguear("propio@test.com", "123456");
        
        UsuarioEntity propio = usuarioJpaRepository.findByEmail("propio@test.com").get();
        Lista listaPropia = listaRepository.guardar(new Lista(null, "Mi lista", propio.getId()));

        mockMvc.perform(get("/api/listas/" + listaPropia.id()) // realiza una petición GET al endpoint de listas con el ID de la lista propia
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void acceder_aListaAjena_devuelve403() throws Exception {
        // metodo para probar que un usuario no puede acceder a una lista ajena y recibe un estado 403 (prohibido)
        String tokenBob = registrarYLoguear("bob.intruso@test.com", "123456");

        mockMvc.perform(get("/api/listas/" + listaUser1Id)
                        .header("Authorization", "Bearer " + tokenBob))
                .andExpect(status().isForbidden());
    }

    @Test
    void acceder_sinToken_devuelve401() throws Exception {
        // metodo para probar que un usuario no autenticado no puede acceder a las listas y recibe un estado 401 (no autorizado)
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void health_sinToken_devuelveEstadoOk() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.service").value("demo"));
    }

    @Test
    void acceder_adminAListaAjena_devuelve200() throws Exception {
        // metodo para probar que un usuario con rol ADMIN puede acceder a una lista ajena y recibe un estado 200 (OK)
        LoginRequestDTO loginAdmin = new LoginRequestDTO("admin@test.com", "123456");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginAdmin)))
                .andReturn();
        String tokenAdmin = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText(); // extrae el token de la respuesta del login del admin

        mockMvc.perform(get("/api/listas/" + listaUser1Id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }


}
