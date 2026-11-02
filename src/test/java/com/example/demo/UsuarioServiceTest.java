package com.example.demo;

import com.example.demo.dto.RegistroRequestDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.services.JwtService;
import com.example.demo.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

//imports estáticos para facilitar las aserciones y el uso de Mockito
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para UsuarioService.
 * test unitario que verifica que al registrar un usuario, la contraseña se guarde como hash y no en texto plano
 */

class UsuarioServiceTest {
    
    @Test
    void registrarUsuario_conPasswordTextoPlano_guardaHash(){
        //preparación de los mocks (arrange)
        UsuarioRepository repository = mock(UsuarioRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService= mock(JwtService.class);
        UsuarioService usuarioService = new UsuarioService(repository, encoder, jwtService);

        RegistroRequestDTO dto = new RegistroRequestDTO("usuario1", "password123");
        when(repository.existePorEmail(dto.email())).thenReturn(false);
        when(encoder.encode(dto.password())).thenReturn("hash_falso_123");

        //ejecución del método a probar (act)
        usuarioService.registrarUsuario(dto);

        //verificación de que se llamó al método guardar con el hash correcto (assert)
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).guardar(captor.capture());
        Usuario usuarioGuardado = captor.getValue();

        assertNotEquals("password123", usuarioGuardado.passwordHash()); // verifica que no se guardó la contraseña en texto plano
        assertEquals("hash_falso_123", usuarioGuardado.passwordHash()); // verifica que se guardó el hash correcto
    }
}
