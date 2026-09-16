package com.runa.runa.TestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.runa.runa.model.entity.Usuario;
import com.runa.runa.repository.UsuarioRepository;
import com.runa.runa.service.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Eduardo");
        usuario.setApellido("Peña");
        usuario.setEmail("eduardo@test.com");
        usuario.setTelefono("999999");
        usuario.setNacionalidad("Perú");
        usuario.setActivo(true);
        usuario.setRol("ADMIN");
    }

    @Test
    void testFindAll() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findAll();

        assertEquals(1, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testFindById() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Eduardo", result.get().getNombre());
    }

    @Test
    void testFindById_NotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testSave_Exito() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario result = usuarioService.save(usuario);

        assertNotNull(result);
        assertEquals("Eduardo", result.getNombre());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testSave_EmailDuplicado() {
        when(usuarioRepository.existsByEmail(usuario.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Ya existe un usuario con el email: eduardo@test.com", ex.getMessage());
    }

    @Test
    void testUpdate_Exito() {
        Usuario cambios = new Usuario();
        cambios.setNombre("NuevoNombre");
        cambios.setApellido("NuevoApellido");
        cambios.setTelefono("123456");
        cambios.setNacionalidad("Chile");
        cambios.setRol("USER");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.update(1L, cambios);

        assertEquals("NuevoNombre", result.getNombre());
        assertEquals("NuevoApellido", result.getApellido());
        assertEquals("USER", result.getRol());
    }

    @Test
    void testUpdate_NoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.update(1L, usuario));

        assertEquals("Usuario no encontrado con ID: 1", ex.getMessage());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.deleteById(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testFindByEmail() {
        when(usuarioRepository.findByEmail("edu@test.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findByEmail("edu@test.com");

        assertTrue(result.isPresent());
        assertEquals("Eduardo", result.get().getNombre());
    }

    @Test
    void testExistsByEmail() {
        when(usuarioRepository.existsByEmail("x@test.com")).thenReturn(true);

        assertTrue(usuarioService.existsByEmail("x@test.com"));
    }

    @Test
    void testFindByRol() {
        when(usuarioRepository.findByRol("ADMIN")).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findByRol("ADMIN");

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------
    // findActivos()
    // ---------------------------------------------------------
    @Test
    void testFindActivos() {
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findActivos();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActivo());
    }

    // ---------------------------------------------------------
    // findByNombreOrApellido()
    // ---------------------------------------------------------
    @Test
    void testFindByNombreOrApellido() {
        when(usuarioRepository.findByNombreOrApellidoContainingIgnoreCase("edu"))
                .thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.findByNombreOrApellido("edu");

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------
    // desactivarUsuario()
    // ---------------------------------------------------------
    @Test
    void testDesactivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.desactivarUsuario(1L);

        assertFalse(result.getActivo());
    }

    @Test
    void testDesactivarUsuario_NoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.desactivarUsuario(1L));

        assertEquals("Usuario no encontrado con ID: 1", ex.getMessage());
    }

    @Test
    void testActivarUsuario() {
        usuario.setActivo(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario result = usuarioService.activarUsuario(1L);

        assertTrue(result.getActivo());
    }

    @Test
    void testCountByRol() {
        when(usuarioRepository.countByRol("ADMIN")).thenReturn(3L);

        long result = usuarioService.countByRol("ADMIN");

        assertEquals(3, result);
    }

    @Test
    void testFindByEmailAndActivo() {
        when(usuarioRepository.findByEmailAndActivoTrue("edu@test.com"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.findByEmailAndActivo("edu@test.com");

        assertTrue(result.isPresent());
    }
}
