package com.example.demo;

import com.example.demo.dto.ListaRequestDTO;
import com.example.demo.model.Lista;
import com.example.demo.repository.FavoritoJpaRepository;
import com.example.demo.repository.ListaJpaRepository;
import com.example.demo.repository.ListaRepository;
import com.example.demo.services.ListaService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

//se usan imports estáticos para facilitar las aserciones y el uso de Mockito
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/* Clase de prueba para ListaService 
* test unitario para comprobar que al crear una lista, se asocia automáticamente al dueño autenticado
*/
class ListaServiceTest {
    @Test
    void crearLista_conUsuarioAutenticado_asociaListaADueño(){
        //preparación de los mocks (arrange)
        ListaRepository listaRepository = mock(ListaRepository.class);
        FavoritoJpaRepository favoritoJpaRepository = mock(FavoritoJpaRepository.class);
        ListaJpaRepository listaJpaRepository = mock(ListaJpaRepository.class);
        ListaService service = new ListaService(listaRepository, favoritoJpaRepository, listaJpaRepository);

        Long usuarioIdMock = 99L; // id de usuario simulado
        // Simula un usuario autenticado en el contexto de seguridad
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user@test.com", null, Collections.emptyList());
        auth.setDetails(usuarioIdMock); // se guarda el id del usuario en los detalles de la autenticación
        SecurityContextHolder.getContext().setAuthentication(auth); // se establece la autenticación en el contexto de seguridad

        ListaRequestDTO dto = new ListaRequestDTO("Mi Lista Test");
        when(listaRepository.guardar(any(Lista.class))).thenAnswer(i -> {
            Lista lista = i.getArgument(0);
            return new Lista(1L, lista.nombre(), lista.usuarioId()); // simula que la lista se guarda y se le asigna un id
        });

        //ejecución del método a probar (act)
        service.crearLista(dto);

        //verificación de que se llamó al método guardar con el dueño correcto (assert)
        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).guardar(captor.capture());  

        assertEquals(usuarioIdMock, captor.getValue().usuarioId()); // verifica que la lista se asoció al usuario autenticado

        SecurityContextHolder.clearContext(); // limpia el contexto de seguridad después de la prueba
    }
}
