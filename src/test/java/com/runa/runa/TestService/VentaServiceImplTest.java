
package com.runa.runa.TestService;

import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.entity.Usuario;
import com.runa.runa.model.entity.Venta;

import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.repository.UsuarioRepository;
import com.runa.runa.repository.VentaRepository;

import com.runa.runa.service.InventarioService;
import com.runa.runa.service.impl.VentaServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Producto producto;
    private Usuario usuario;
    private Venta venta;
    private Inventario inventario;

    @BeforeEach
    void configurarDatos() {

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setPrecio(new BigDecimal("10.00"));

        usuario = new Usuario();
        usuario.setIdUsuario(1L);

        venta = new Venta();
        venta.setProducto(producto);
        venta.setUsuario(usuario);
        venta.setCantidad(5);
        venta.setEstado("completada");

        inventario = new Inventario();
        inventario.setIdInventario(1L);
        inventario.setProducto(producto);
        inventario.setStockTotal(100);
        inventario.setStockDisponible(100);
        inventario.setStockMinimo(10);
    }

    // =========================================================
    // PRUEBA 1: REGISTRAR VENTA CORRECTAMENTE
    // =========================================================

    @Test
    void registrarVentaCorrectamente() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(inventarioService.reducirStock(1L, 5))
                .thenReturn(inventario);

        when(ventaRepository.save(any(Venta.class)))
                .thenAnswer(invocacion -> {
                    Venta guardada = invocacion.getArgument(0);
                    guardada.setIdVenta(1L);
                    return guardada;
                });

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertEquals("completada", resultado.getEstado());
        assertEquals(
                0,
                new BigDecimal("50.00").compareTo(resultado.getTotal())
        );

        verify(inventarioService, times(1))
                .reducirStock(1L, 5);

        verify(ventaRepository, times(1))
                .save(any(Venta.class));
    }

    // =========================================================
    // PRUEBA 2: RECHAZAR VENTA SIN STOCK
    // =========================================================

    @Test
    void rechazarVentaSinStock() {

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(inventarioService.reducirStock(1L, 5))
                .thenThrow(new IllegalStateException(
                        "No hay stock disponible suficiente"
                ));

        assertThrows(
                IllegalStateException.class,
                () -> ventaService.save(venta)
        );

        verify(ventaRepository, never())
                .save(any(Venta.class));
    }

    // =========================================================
    // PRUEBA 3: RECHAZAR CANTIDAD INVALIDA
    // =========================================================

    @Test
    void rechazarCantidadInvalida() {

        venta.setCantidad(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.save(venta)
        );

        verifyNoInteractions(
                productoRepository,
                usuarioRepository,
                inventarioService,
                ventaRepository
        );
    }

    // =========================================================
    // PRUEBA 4: CANCELAR VENTA CORRECTAMENTE
    // =========================================================

    @Test
    void cancelarVentaCorrectamente() {

        venta.setIdVenta(1L);
        venta.setEstado("completada");

        when(ventaRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(venta));

        when(inventarioService.devolverStock(1L, 5))
                .thenReturn(inventario);

        when(ventaRepository.save(any(Venta.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        Venta resultado = ventaService.cancelarVenta(1L);

        assertEquals("cancelada", resultado.getEstado());

        verify(inventarioService, times(1))
                .devolverStock(1L, 5);

        verify(ventaRepository, times(1))
                .save(venta);
    }

    // =========================================================
    // PRUEBA 5: NO CANCELAR DOS VECES
    // =========================================================

    @Test
    void rechazarCancelacionDuplicada() {

        venta.setIdVenta(1L);
        venta.setEstado("cancelada");

        when(ventaRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(venta));

        assertThrows(
                IllegalStateException.class,
                () -> ventaService.cancelarVenta(1L)
        );

        verify(inventarioService, never())
                .devolverStock(anyLong(), anyInt());

        verify(ventaRepository, never())
                .save(any(Venta.class));
    }

    // =========================================================
    // PRUEBA 6: ELIMINACION LOGICA
    // =========================================================

    @Test
    void eliminarVentaConservandoHistorial() {

        venta.setIdVenta(1L);
        venta.setEstado("completada");

        when(ventaRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(venta));

        when(inventarioService.devolverStock(1L, 5))
                .thenReturn(inventario);

        when(ventaRepository.save(any(Venta.class)))
                .thenAnswer(invocacion ->
                        invocacion.getArgument(0)
                );

        ventaService.deleteById(1L);

        assertEquals("cancelada", venta.getEstado());

        verify(ventaRepository, never())
                .delete(any(Venta.class));

        verify(inventarioService)
                .devolverStock(1L, 5);
    }
}
