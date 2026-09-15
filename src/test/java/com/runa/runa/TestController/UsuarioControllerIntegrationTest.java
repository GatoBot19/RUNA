package com.runa.runa.TestController;

import com.runa.runa.model.entity.Usuario;
import com.runa.runa.model.dto.UsuarioRequest;
import com.runa.runa.repository.UsuarioRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        usuarioRepository.deleteAll();

        Usuario admin = new Usuario();
        admin.setEmail("admin@mail.com");
        admin.setPassword("12345");
        usuarioRepository.save(admin);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsuariosIntegration() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].email").value("admin@mail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsuarioByIdIntegration() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("example@mail.com");
        usuario.setPassword("secreto");
        usuario = usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/" + usuario.getIdUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("example@mail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUsuarioIntegration() throws Exception {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("nuevo@mail.com");
        request.setPassword("123456");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente"))
                .andExpect(jsonPath("$.data.email").value("nuevo@mail.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUsuarioIntegration() throws Exception {
        Usuario u = new Usuario();
        u.setEmail("delete@mail.com");
        u.setPassword("xx");
        u = usuarioRepository.save(u);

        mockMvc.perform(delete("/api/usuarios/" + u.getIdUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado exitosamente"));
    }
}
