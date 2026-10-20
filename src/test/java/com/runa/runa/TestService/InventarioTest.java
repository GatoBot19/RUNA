
package com.runa.runa.TestService;

import com.runa.runa.model.entity.Inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioTest {

    private Inventario inventario;

    @BeforeEach
    void configurarInventario() {

        inventario = new Inventario();

        inventario.setIdInventario(1L);
        inventario.setStockTotal(100);
        inventario.setStockDisponible(100);
        inventario.setStockMinimo(10);
    }

    // =========================================================
    // PRUEBA 1: REDUCIR STOCK CORRECTAMENTE
    // =========================================================

    @Test
    void reducirStockCorrectamente() {

        inventario.reducirStock(20);

        assertEquals(80, inventario.getStockDisponible());
        assertEquals(100, inventario.getStockTotal());
    }

    // =========================================================
    // PRUEBA 2: RECHAZAR VENTA SIN STOCK
    // =========================================================

    @Test
    void rechazarStockInsuficiente() {

        assertThrows(
                IllegalStateException.class,
                () -> inventario.reducirStock(150)
        );

        assertEquals(100, inventario.getStockDisponible());
    }

    // =========================================================
    // PRUEBA 3: AUMENTAR STOCK POR REPOSICION
    // =========================================================

    @Test
    void aumentarStockCorrectamente() {

        inventario.aumentarStock(50);

        assertEquals(150, inventario.getStockTotal());
        assertEquals(150, inventario.getStockDisponible());
    }

    // =========================================================
    // PRUEBA 4: DEVOLVER STOCK POR CANCELACION
    // =========================================================

    @Test
    void devolverStockCorrectamente() {

        inventario.reducirStock(20);
        inventario.devolverStock(20);

        assertEquals(100, inventario.getStockTotal());
        assertEquals(100, inventario.getStockDisponible());
    }

    // =========================================================
    // PRUEBA 5: NO DEVOLVER MAS DEL TOTAL
    // =========================================================

    @Test
    void rechazarDevolucionExcesiva() {

        assertThrows(
                IllegalStateException.class,
                () -> inventario.devolverStock(20)
        );

        assertEquals(100, inventario.getStockDisponible());
    }

    // =========================================================
    // PRUEBA 6: DETECTAR STOCK MINIMO
    // =========================================================

    @Test
    void detectarStockMinimo() {

        inventario.reducirStock(90);

        assertTrue(inventario.necesitaReposicion());
        assertEquals(10, inventario.getStockDisponible());
    }

    // =========================================================
    // PRUEBA 7: RECHAZAR CANTIDADES INVALIDAS
    // =========================================================

    @Test
    void rechazarCantidadesInvalidas() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventario.reducirStock(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventario.aumentarStock(-5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventario.devolverStock(null)
        );
    }
}
