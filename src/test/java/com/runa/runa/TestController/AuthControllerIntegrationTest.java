
package com.runa.runa.TestController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.runa.runa.model.dto.AuthDTO;
import com.runa.runa.model.dto.UsuarioDTO;
import com.runa.runa.model.entity.Usuario;
import com.runa.runa.model.mapper.UsuarioMapper;
import com.runa.runa.security.JwtTokenUtil;
import com.runa.runa.service.UsuarioService;
import com.runa.runa.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioMapper usuarioMapper;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private SecurityUtil securityUtil;

    // PRUEBA 1: Inicio de sesión exitoso
    @Test
    void testLoginSuccess() throws Exception {

        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("123456");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail("test@example.com");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);

        when(auth.getPrincipal())
                .thenReturn(userDetails);

        when(usuarioService.findByEmail("test@example.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toDto(usuario))
                .thenReturn(usuarioDTO);

        when(jwtTokenUtil.generateToken(userDetails))
                .thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token")
                        .value("fake-jwt-token"))
                .andExpect(jsonPath("$.data.usuario.email")
                        .value("test@example.com"));
    }

    // PRUEBA 2: Inicio de sesión con credenciales incorrectas
    @Test
    void testLoginFail() throws Exception {

        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("bad@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(
                        new BadCredentialsException("Bad credentials")
                );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // PRUEBA 3: Registro exitoso de un usuario
    @Test
    void testRegisterSuccess() throws Exception {

        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();

        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setEmail("nuevo@example.com");
        request.setPassword("123456");
        request.setTelefono("999999999");
        request.setNacionalidad("Peru");

        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@example.com");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail("nuevo@example.com");

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(usuarioService.existsByEmail("nuevo@example.com"))
                .thenReturn(false);

        when(usuarioService.save(any()))
                .thenReturn(usuario);

        when(authenticationManager.authenticate(any()))
                .thenReturn(auth);

        when(auth.getPrincipal())
                .thenReturn(userDetails);

        when(jwtTokenUtil.generateToken(userDetails))
                .thenReturn("token-123");

        when(usuarioMapper.toDto(usuario))
                .thenReturn(usuarioDTO);

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token")
                        .value("token-123"))
                .andExpect(jsonPath("$.data.usuario.email")
                        .value("nuevo@example.com"));
    }

    // PRUEBA 4: Registro con correo electrónico duplicado
    @Test
    void testRegisterEmailExists() throws Exception {

        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();

        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setEmail("existe@example.com");
        request.setPassword("123456");
        request.setTelefono("999999999");
        request.setNacionalidad("Peru");

        when(usuarioService.existsByEmail("existe@example.com"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("El email ya está registrado"));
    }

    // PRUEBA 5: Verificación de autenticación
    @Test
    void testCheckAuth() throws Exception {

        mockMvc.perform(get("/api/auth/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value("Token válido"));
    }

    // PRUEBA 6: Obtener perfil del usuario autenticado
    @Test
    void testGetProfile() throws Exception {

        Usuario usuario = new Usuario();
        usuario.setEmail("perfil@example.com");

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail("perfil@example.com");

        when(securityUtil.getCurrentUser())
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toDto(usuario))
                .thenReturn(usuarioDTO);

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email")
                        .value("perfil@example.com"));
    }

    // PRUEBA 7: Acceso al perfil sin autenticación
    @Test
    void testGetProfileUnauthorized() throws Exception {

        when(securityUtil.getCurrentUser())
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().is4xxClientError());
    }
}
