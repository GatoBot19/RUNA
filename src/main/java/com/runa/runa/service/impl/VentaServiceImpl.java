
package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Venta;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.entity.Usuario;

import com.runa.runa.repository.VentaRepository;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.repository.UsuarioRepository;

import com.runa.runa.service.InventarioService;
import com.runa.runa.service.VentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    // =========================================================
    // 1. LISTAR TODAS LAS VENTAS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {

        log.info("Consultando todas las ventas");

        return ventaRepository.findAll();
    }

    // =========================================================
    // 2. BUSCAR VENTA POR ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> findById(Long id) {

        return ventaRepository.findById(id);
    }

    // =========================================================
    // 3. REGISTRAR NUEVA VENTA
    // =========================================================

    @Override
    @Transactional
    public Venta save(Venta venta) {

        if (venta == null) {
            throw new IllegalArgumentException(
                    "Los datos de la venta son obligatorios"
            );
        }

        if (venta.getIdVenta() != null) {
            throw new IllegalArgumentException(
                    "Una venta nueva no debe tener ID"
            );
        }

        if (venta.getUsuario() == null ||
                venta.getUsuario().getIdUsuario() == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio"
            );
        }

        if (venta.getProducto() == null ||
                venta.getProducto().getIdProducto() == null) {
            throw new IllegalArgumentException(
                    "El producto es obligatorio"
            );
        }

        if (venta.getCantidad() == null ||
                venta.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        if (venta.getEstado() != null &&
                !venta.getEstado().isBlank() &&
                !"completada".equalsIgnoreCase(venta.getEstado())) {

            throw new IllegalArgumentException(
                    "Las ventas nuevas deben estar completadas"
            );
        }

        Long idProducto = venta.getProducto().getIdProducto();
        Long idUsuario = venta.getUsuario().getIdUsuario();

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado: " + idProducto
                        )
                );

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado: " + idUsuario
                        )
                );

        BigDecimal precio = producto.getPrecio();

        if (precio == null || precio.signum() < 0) {
            throw new IllegalArgumentException(
                    "El producto no tiene un precio valido"
            );
        }

        // Descontar unidades disponibles.
        inventarioService.reducirStock(
                idProducto,
                venta.getCantidad()
        );

        venta.setProducto(producto);
        venta.setUsuario(usuario);
        venta.setPrecioUnitario(precio);
        venta.calcularTotal();

        venta.setEstado("completada");

        LocalDateTime ahora = LocalDateTime.now();

        venta.setFechaVenta(ahora);
        venta.setFechaCreacion(ahora);

        Venta guardada = ventaRepository.save(venta);

        log.info(
                "Venta registrada correctamente. ID: {}",
                guardada.getIdVenta()
        );

        return guardada;
    }

    // =========================================================
    // 4. ACTUALIZAR VENTA
    // =========================================================

    @Override
    @Transactional
    public Venta update(Long id, Venta venta) {

        if (venta == null) {
            throw new IllegalArgumentException(
                    "Los datos son obligatorios"
            );
        }

        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venta no encontrada: " + id
                        )
                );

        if (venta.getCantidad() != null &&
                !venta.getCantidad().equals(existente.getCantidad())) {

            throw new IllegalStateException(
                    "No se puede modificar la cantidad"
            );
        }

        if (venta.getProducto() != null) {

            Long nuevoId = venta.getProducto().getIdProducto();
            Long idActual = existente.getProducto().getIdProducto();

            if (!idActual.equals(nuevoId)) {
                throw new IllegalStateException(
                        "No se puede cambiar el producto"
                );
            }
        }

        if (venta.getEstado() != null &&
                !venta.getEstado().equalsIgnoreCase(existente.getEstado())) {

            throw new IllegalStateException(
                    "Use los metodos especificos para cambiar el estado"
            );
        }

        // No se modifican los datos economicos originales.
        return existente;
    }

    // =========================================================
    // 5. ELIMINAR VENTA
    // Se realiza una cancelacion logica
    // =========================================================

    @Override
    @Transactional
    public void deleteById(Long id) {

        log.info(
                "Solicitando eliminacion logica de venta ID: {}",
                id
        );

        cancelarVenta(id);

        log.info(
                "Venta conservada como cancelada. ID: {}",
                id
        );
    }

    // =========================================================
    // 6. CONSULTAS DE VENTAS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByUsuario(Long idUsuario) {

        return ventaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByProducto(Long idProducto) {

        return ventaRepository.findByProductoIdProducto(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByCategoria(Long idCategoria) {

        return ventaRepository.findByProductoCategoriaIdCategoria(
                idCategoria
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByEstado(String estado) {

        return ventaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByUsuarioAndEstado(
            Long idUsuario,
            String estado) {

        return ventaRepository.findByUsuarioIdUsuarioAndEstado(
                idUsuario,
                estado
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByRangoFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return ventaRepository.findByFechaVentaBetween(
                fechaInicio,
                fechaFin
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findVentasRecientes(
            LocalDateTime fecha) {

        return ventaRepository.findVentasRecientes(fecha);
    }

    // =========================================================
    // 7. ESTADISTICAS DE VENTAS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countByEstado(String estado) {

        return ventaRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public Long sumarProductosVendidos() {

        return ventaRepository.sumarProductosVendidos();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumarIngresos() {

        return ventaRepository.sumarIngresos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorMes(int year) {

        return ventaRepository.obtenerEstadisticasPorMes(year);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> buscarPorProductoYRangoFechas(
            Long idProducto,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return ventaRepository.buscarPorProductoYRangoFechas(
                idProducto,
                fechaInicio,
                fechaFin
        );
    }

    // =========================================================
    // 8. CANCELAR VENTA
    // Protegida con bloqueo pesimista
    // =========================================================

    @Override
    @Transactional
    public Venta cancelarVenta(Long id) {

        // Bloquear el registro antes de revisar su estado.
        Venta venta = ventaRepository.findByIdWithLock(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venta no encontrada: " + id
                        )
                );

        if ("cancelada".equalsIgnoreCase(venta.getEstado())) {

            throw new IllegalStateException(
                    "La venta ya esta cancelada"
            );
        }

        if (!"completada".equalsIgnoreCase(venta.getEstado())) {

            throw new IllegalStateException(
                    "Solo se pueden cancelar ventas completadas"
            );
        }

        // Restituir stock sin aumentar el stock total.
        inventarioService.devolverStock(
                venta.getProducto().getIdProducto(),
                venta.getCantidad()
        );

        venta.setEstado("cancelada");

        Venta cancelada = ventaRepository.save(venta);

        log.info(
                "Venta cancelada correctamente. ID: {}",
                cancelada.getIdVenta()
        );

        return cancelada;
    }

    // =========================================================
    // 9. COMPLETAR VENTA
    // =========================================================

    @Override
    @Transactional
    public Venta completarVenta(Long id) {

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Venta no encontrada: " + id
                        )
                );

        if ("completada".equalsIgnoreCase(venta.getEstado())) {

            throw new IllegalStateException(
                    "La venta ya esta completada"
            );
        }

        if ("cancelada".equalsIgnoreCase(venta.getEstado())) {

            throw new IllegalStateException(
                    "No se puede completar una venta cancelada"
            );
        }

        // No se habilitan ventas pendientes hasta definir
        // las reglas correspondientes de inventario.
        throw new IllegalStateException(
                "El flujo de ventas pendientes no esta habilitado"
        );
    }
}
