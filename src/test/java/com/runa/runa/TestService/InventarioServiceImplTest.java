
package com.runa.runa.TestService;

import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Producto;

import com.runa.runa.repository.InventarioRepository;
import com.runa.runa.repository.ProductoRepository;

import com.runa.runa.service.impl.InventarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Inventario inventario;

    @BeforeEach
    void configurarDatos() {

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Arroz");

        inventario = new Inventario();
        inventario.setIdInventario(1L);
        inventario.setProducto(producto);
        inventario.setStockTotal(100);
        inventario.setStockDisponible(100);
        inventario.setStockMinimo(10);
    }

    // =========================================================
    // PRUEBA 1: CREAR INVENTARIO CORRECTAMENTE
    // =========================================================

    @Test
    void crearInventarioCorrectamente() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(inventarioRepository.existsByProductoIdProducto(1L))
                .thenReturn(false);

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.save(
                inventario, 1L
        );

        assertNotNull(resultado);
        assertEquals(100, resultado.getStockTotal());
        assertEquals(100, resultado.getStockDisponible());
        assertEquals(producto, resultado.getProducto());

        verify(inventarioRepository).save(inventario);
    }

    // =========================================================
    // PRUEBA 2: IMPEDIR INVENTARIO DUPLICADO
    // =========================================================

    @Test
    void rechazarInventarioDuplicado() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(inventarioRepository.existsByProductoIdProducto(1L))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> inventarioService.save(inventario, 1L)
        );

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    // =========================================================
    // PRUEBA 3: REDUCIR STOCK POR VENTA
    // =========================================================

    @Test
    void reducirStockCorrectamente() {

        when(inventarioRepository.findByProductoWithLock(1L))
                .thenReturn(Optional.of(inventario));

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.reducirStock(
                1L, 20
        );

        assertEquals(80, resultado.getStockDisponible());
        assertEquals(100, resultado.getStockTotal());

        verify(inventarioRepository)
                .findByProductoWithLock(1L);

        verify(inventarioRepository)
                .save(inventario);
    }

    // =========================================================
    // PRUEBA 4: RECHAZAR STOCK INSUFICIENTE
    // =========================================================

    @Test
    void rechazarStockInsuficiente() {

        when(inventarioRepository.findByProductoWithLock(1L))
                .thenReturn(Optional.of(inventario));

        assertThrows(
                IllegalStateException.class,
                () -> inventarioService.reducirStock(1L, 150)
        );

        assertEquals(100, inventario.getStockDisponible());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    // =========================================================
    // PRUEBA 5: AUMENTAR STOCK POR REPOSICION
    // =========================================================

    @Test
    void aumentarStockCorrectamente() {

        when(inventarioRepository.findByProductoWithLock(1L))
                .thenReturn(Optional.of(inventario));

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.aumentarStock(
                1L, 50
        );

        assertEquals(150, resultado.getStockTotal());
        assertEquals(150, resultado.getStockDisponible());

        verify(inventarioRepository)
                .save(inventario);
    }

    // =========================================================
    // PRUEBA 6: DEVOLVER STOCK POR CANCELACION
    // =========================================================

    @Test
    void devolverStockCorrectamente() {

        inventario.setStockDisponible(80);

        when(inventarioRepository.findByProductoWithLock(1L))
                .thenReturn(Optional.of(inventario));

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.devolverStock(
                1L, 20
        );

        assertEquals(100, resultado.getStockDisponible());
        assertEquals(100, resultado.getStockTotal());

        verify(inventarioRepository)
                .save(inventario);
    }

    // =========================================================
    // PRUEBA 7: IMPEDIR DEVOLUCION EXCESIVA
    // =========================================================

    @Test
    void rechazarDevolucionExcesiva() {

        when(inventarioRepository.findByProductoWithLock(1L))
                .thenReturn(Optional.of(inventario));

        assertThrows(
                IllegalStateException.class,
                () -> inventarioService.devolverStock(1L, 20)
        );

        assertEquals(100, inventario.getStockDisponible());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    // =========================================================
    // PRUEBA 8: VERIFICAR DISPONIBILIDAD
    // =========================================================

    @Test
    void verificarStockDisponible() {

        when(inventarioRepository.findStockDisponibleByProducto(1L))
                .thenReturn(100);

        assertTrue(
                inventarioService.verificarDisponibilidad(1L, 20)
        );

        assertFalse(
                inventarioService.verificarDisponibilidad(1L, 150)
        );
    }

    // =========================================================
    // PRUEBA 9: RECHAZAR CANTIDADES INVALIDAS
    // =========================================================

    @Test
    void rechazarCantidadesInvalidas() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reducirStock(1L, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.aumentarStock(1L, -5)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.devolverStock(1L, null)
        );

        verifyNoInteractions(inventarioRepository);
    }

    // =========================================================
    // PRUEBA 10: ACTUALIZAR INVENTARIO CORRECTAMENTE
    // CORRECCION DEL ERROR ANTERIOR
    // =========================================================

    @Test
    void actualizarInventarioCorrectamente() {

        Inventario cambios = new Inventario();

        // Conservamos los valores actuales del inventario.
        cambios.setStockTotal(100);
        cambios.setStockDisponible(100);

        // Modificamos el stock minimo.
        cambios.setStockMinimo(15);

        when(inventarioRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(inventario));

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Inventario resultado = inventarioService.update(
                1L, cambios, 1L
        );

        assertNotNull(resultado);
        assertEquals(15, resultado.getStockMinimo());
        assertEquals(100, resultado.getStockDisponible());
        assertEquals(100, resultado.getStockTotal());

        verify(inventarioRepository)
                .findByIdWithLock(1L);

        verify(inventarioRepository)
                .save(inventario);
    }

    // =========================================================
    // PRUEBA 11: IMPEDIR CAMBIAR EL PRODUCTO
    // =========================================================

    @Test
    void rechazarCambioDeProducto() {

        Inventario cambios = new Inventario();
        cambios.setStockMinimo(15);

        when(inventarioRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(inventario));

        assertThrows(
                IllegalStateException.class,
                () -> inventarioService.update(1L, cambios, 2L)
        );

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    // =========================================================
    // PRUEBA 12: ELIMINAR INVENTARIO
    // =========================================================

    @Test
    void eliminarInventarioCorrectamente() {

        when(inventarioRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(inventario));

        inventarioService.deleteById(1L);

        verify(inventarioRepository)
                .findByIdWithLock(1L);

        verify(inventarioRepository)
                .delete(inventario);
    }
}
