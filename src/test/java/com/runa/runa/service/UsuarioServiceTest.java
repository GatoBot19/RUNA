// src/test/java/com/runa/runa/service/UsuarioServiceTest.java
package com.runa.runa.service;

import com.runa.runa.model.entity.Usuario;
import com.runa.runa.repository.UsuarioRepository;
import com.runa.runa.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void cuandoBuscarUsuarioPorEmail_yExiste_entoncesRetornaUsuario() {
        // Given - Configurar con records modernos
        var usuario = Usuario.builder()
                .idUsuario(1L)
                .nombre("Ana")
                .email("ana@test.com")
                .build();

        when(usuarioRepository.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(usuario));

        // When - Ejecutar
        var resultado = usuarioService.findByEmail("ana@test.com");

        // Then - Verificar con Java 21 features
        assertTrue(resultado.isPresent());
        assertEquals("Ana", resultado.get().getNombre());

        // Verificación moderna
        verify(usuarioRepository, times(1)).findByEmail("ana@test.com");
    }

    @Test
    void cuandoBuscarUsuarioPorEmail_yNoExiste_entoncesRetornaVacio() {
        // Given
        when(usuarioRepository.findByEmail("noexiste@test.com"))
                .thenReturn(Optional.empty());

        // When
        var resultado = usuarioService.findByEmail("noexiste@test.com");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void cuandoVerificarEmailExistente_entoncesRetornaTrue() {
        // Given
        when(usuarioRepository.existsByEmail("existente@test.com"))
                .thenReturn(true);

        // When
        var resultado = usuarioService.existsByEmail("existente@test.com");

        // Then
        assertTrue(resultado);
    }
}
