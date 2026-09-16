package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Venta;
import com.runa.runa.repository.VentaRepository;
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
    private final InventarioService inventarioService;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        log.info("Buscando todas las ventas");
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> findById(Long id) {
        log.info("Buscando venta con ID: {}", id);
        return ventaRepository.findById(id);
    }

    @Override
    @Transactional
    public Venta save(Venta venta) {

        log.info(
                "Registrando venta del producto ID: {}",
                venta.getProducto().getIdProducto()
        );

        if (venta.getUsuario() == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio"
            );
        }

        if (venta.getProducto() == null) {
            throw new IllegalArgumentException(
                    "El producto es obligatorio"
            );
        }

        if (venta.getCantidad() == null || venta.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }

        // El precio se obtiene directamente del producto.
        venta.setPrecioUnitario(
                venta.getProducto().getPrecio()
        );

        if (venta.getPrecioUnitario() == null) {
            throw new IllegalArgumentException(
                    "El producto no tiene un precio registrado"
            );
        }

        Long idProducto = venta.getProducto().getIdProducto();

        // Verificar disponibilidad.
        boolean disponible =
                inventarioService.verificarDisponibilidad(
                        idProducto,
                        venta.getCantidad()
                );

        if (!disponible) {
            throw new IllegalStateException(
                    "No hay stock disponible suficiente para el producto: "
                            + venta.getProducto().getNombre()
            );
        }

        // Descontar stock.
        inventarioService.reducirStock(
                idProducto,
                venta.getCantidad()
        );

        // Calcular total.
        venta.calcularTotal();

        if (venta.getEstado() == null
                || venta.getEstado().isBlank()) {
            venta.setEstado("completada");
        }

        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }

        if (venta.getFechaCreacion() == null) {
            venta.setFechaCreacion(LocalDateTime.now());
        }

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Venta update(Long id, Venta venta) {

        log.info("Actualizando venta con ID: {}", id);

        Venta existente = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venta no encontrada con ID: " + id
                        )
                );

        // No modificar producto ni cantidad de una venta completada.
        if ("completada".equalsIgnoreCase(existente.getEstado())) {

            if (venta.getCantidad() != null
                    && !venta.getCantidad().equals(
                            existente.getCantidad())) {

                throw new IllegalStateException(
                        "No se puede modificar la cantidad de una venta completada"
                );
            }

            if (venta.getProducto() != null
                    && !venta.getProducto().getIdProducto().equals(
                            existente.getProducto().getIdProducto())) {

                throw new IllegalStateException(
                        "No se puede cambiar el producto de una venta completada"
                );
            }
        }

        if (venta.getEstado() != null) {
            existente.setEstado(venta.getEstado());
        }

        return ventaRepository.save(existente);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        log.info("Eliminando venta con ID: {}", id);

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venta no encontrada con ID: " + id
                        )
                );

        // Si eliminamos una venta completada,
        // devolvemos el stock.
        if ("completada".equalsIgnoreCase(venta.getEstado())) {

            inventarioService.aumentarStock(
                    venta.getProducto().getIdProducto(),
                    venta.getCantidad()
            );
        }

        ventaRepository.deleteById(id);
    }

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

        return ventaRepository
                .findByUsuarioIdUsuarioAndEstado(
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

    @Override
    @Transactional
    public Venta cancelarVenta(Long id) {

        log.info("Cancelando venta con ID: {}", id);

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venta no encontrada con ID: " + id
                        )
                );

        if ("cancelada".equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalStateException(
                    "La venta ya está cancelada"
            );
        }

        // Devolver stock solamente si estaba completada.
        if ("completada".equalsIgnoreCase(venta.getEstado())) {

            inventarioService.aumentarStock(
                    venta.getProducto().getIdProducto(),
                    venta.getCantidad()
            );
        }

        venta.setEstado("cancelada");

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Venta completarVenta(Long id) {

        log.info("Completando venta con ID: {}", id);

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venta no encontrada con ID: " + id
                        )
                );

        if ("completada".equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalStateException(
                    "La venta ya está completada"
            );
        }

        if ("cancelada".equalsIgnoreCase(venta.getEstado())) {
            throw new IllegalStateException(
                    "No se puede completar una venta cancelada"
            );
        }

        venta.setEstado("completada");

        return ventaRepository.save(venta);
    }
}