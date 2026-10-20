
package com.runa.runa.TestService;

import com.runa.runa.model.entity.Categoria;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.entity.Usuario;
import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Venta;

import com.runa.runa.repository.CategoriaRepository;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.repository.UsuarioRepository;
import com.runa.runa.repository.InventarioRepository;
import com.runa.runa.repository.VentaRepository;

import com.runa.runa.service.VentaService;
import com.runa.runa.service.InventarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VentaInventarioIntegrationTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private InventarioService inventarioService;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void prepararDatos() {

        Categoria categoria = new Categoria();
        categoria.setNombre("Productos de prueba");
        categoria.setDescripcion("Categoria para integracion");
        categoria = categoriaRepository.save(categoria);

        producto = new Producto();
        producto.setNombre("Producto de prueba");
        producto.setDescripcion("Producto para ventas");
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setEstado("activo");
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);

        usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellido("Prueba");
        usuario.setEmail(
            "prueba_" + java.util.UUID.randomUUID() + "@runa.test"
        );
        usuario.setPassword("password-prueba");
        usuario.setRol("cliente");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Inventario inventario = new Inventario();
        inventario.setStockTotal(100);
        inventario.setStockDisponible(100);
        inventario.setStockMinimo(10);

        inventarioService.save(
            inventario,
            producto.getIdProducto()
        );
    }

    private Venta crearVenta(Integer cantidad) {

        Venta venta = new Venta();
        venta.setProducto(producto);
        venta.setUsuario(usuario);
        venta.setCantidad(cantidad);

        return venta;
    }

    @Test
    void registrarVentaDebeDescontarInventario() {

        Venta venta = ventaService.save(crearVenta(20));

        assertNotNull(venta.getIdVenta());
        assertEquals("completada", venta.getEstado());
        assertEquals(
            0,
            new BigDecimal("200.00").compareTo(venta.getTotal())
        );

        Inventario inventario = inventarioRepository
            .findByProductoIdProducto(producto.getIdProducto())
            .orElseThrow();

        assertEquals(100, inventario.getStockTotal());
        assertEquals(80, inventario.getStockDisponible());

        assertTrue(ventaRepository.existsById(venta.getIdVenta()));
    }

    @Test
    void cancelarVentaDebeDevolverInventario() {

        Venta venta = ventaService.save(crearVenta(20));

        Venta cancelada = ventaService.cancelarVenta(
            venta.getIdVenta()
        );

        assertEquals("cancelada", cancelada.getEstado());

        Inventario inventario = inventarioRepository
            .findByProductoIdProducto(producto.getIdProducto())
            .orElseThrow();

        assertEquals(100, inventario.getStockTotal());
        assertEquals(100, inventario.getStockDisponible());
    }

    @Test
    void ventaSinStockDebeSerRechazada() {

        assertThrows(
            IllegalStateException.class,
            () -> ventaService.save(crearVenta(150))
        );

        Inventario inventario = inventarioRepository
            .findByProductoIdProducto(producto.getIdProducto())
            .orElseThrow();

        assertEquals(100, inventario.getStockTotal());
        assertEquals(100, inventario.getStockDisponible());
    }

    @Test
    void cancelarDosVecesDebeSerRechazado() {

        Venta venta = ventaService.save(crearVenta(20));

        ventaService.cancelarVenta(venta.getIdVenta());

        assertThrows(
            IllegalStateException.class,
            () -> ventaService.cancelarVenta(venta.getIdVenta())
        );

        Inventario inventario = inventarioRepository
            .findByProductoIdProducto(producto.getIdProducto())
            .orElseThrow();

        assertEquals(100, inventario.getStockDisponible());
    }

    @Test
    void eliminarVentaDebeConservarRegistroCancelado() {

        Venta venta = ventaService.save(crearVenta(10));

        ventaService.deleteById(venta.getIdVenta());

        Venta registrada = ventaRepository
            .findById(venta.getIdVenta())
            .orElseThrow();

        assertEquals("cancelada", registrada.getEstado());

        Inventario inventario = inventarioRepository
            .findByProductoIdProducto(producto.getIdProducto())
            .orElseThrow();

        assertEquals(100, inventario.getStockDisponible());
    }
}
