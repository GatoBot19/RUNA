// Crea este archivo: src/test/java/com/runa/runa/SimpleTest.java
package com.runa.runa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {

    @Test
    void testSumaBasica() {
        assertEquals(5, 2 + 3);
    }

    @Test
    void testUsuarioBuilder() {
        // Probamos que nuestras entidades se crean correctamente
        var usuario = com.runa.runa.model.entity.Usuario.builder()
                .nombre("Juan")
                .email("juan@test.com")
                .build();

        assertNotNull(usuario);
        assertEquals("Juan", usuario.getNombre());
    }
}
