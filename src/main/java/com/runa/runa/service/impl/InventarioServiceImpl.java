package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.repository.InventarioRepository;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.service.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    // =========================================================
    // LISTAR TODO EL INVENTARIO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findAll() {
        log.info("Buscando todo el inventario");
        return inventarioRepository.findAll();
    }

    // =========================================================
    // BUSCAR INVENTARIO POR ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventario> findById(Long id) {
        log.info("Buscando inventario con ID: {}", id);
        return inventarioRepository.findById(id);
    }

    // =========================================================
    // CREAR INVENTARIO
    // =========================================================

    @Override
    @Transactional
    public Inventario save(Inventario inventario, Long idProducto) {

        log.info("Creando inventario para producto ID: {}", idProducto);

        if (idProducto == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Producto no encontrado con ID: " + idProducto
                        )
                );

        if (inventarioRepository.existsByProductoIdProducto(idProducto)) {
            throw new RuntimeException(
                    "Ya existe un inventario para el producto: "
                            + producto.getNombre()
            );
        }

        if (inventario.getStockTotal() == null) {
            inventario.setStockTotal(0);
        }

        if (inventario.getStockDisponible() == null) {
            inventario.setStockDisponible(inventario.getStockTotal());
        }

        if (inventario.getStockMinimo() == null) {
            inventario.setStockMinimo(0);
        }

        if (inventario.getStockTotal() < 0
                || inventario.getStockDisponible() < 0
                || inventario.getStockMinimo() < 0) {

            throw new IllegalArgumentException(
                    "Los valores de stock no pueden ser negativos"
            );
        }

        if (inventario.getStockDisponible()
                > inventario.getStockTotal()) {

            throw new IllegalArgumentException(
                    "El stock disponible no puede ser mayor al stock total"
            );
        }

        inventario.setProducto(producto);

        return inventarioRepository.save(inventario);
    }

    // =========================================================
    // ACTUALIZAR INVENTARIO
    // =========================================================

    @Override
    @Transactional
    public Inventario update(
            Long id,
            Inventario inventario,
            Long idProducto) {

        log.info("Actualizando inventario ID: {}", id);

        Inventario existente = inventarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventario no encontrado con ID: " + id
                        )
                );

        if (idProducto != null) {

            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Producto no encontrado con ID: "
                                            + idProducto
                            )
                    );

            existente.setProducto(producto);
        }

        if (inventario.getStockTotal() != null) {
            existente.setStockTotal(inventario.getStockTotal());
        }

        if (inventario.getStockDisponible() != null) {
            existente.setStockDisponible(
                    inventario.getStockDisponible()
            );
        }

        if (inventario.getStockMinimo() != null) {
            existente.setStockMinimo(
                    inventario.getStockMinimo()
            );
        }

        if (existente.getStockTotal() < 0
                || existente.getStockDisponible() < 0
                || existente.getStockMinimo() < 0) {

            throw new IllegalArgumentException(
                    "Los valores de stock no pueden ser negativos"
            );
        }

        if (existente.getStockDisponible()
                > existente.getStockTotal()) {

            throw new IllegalArgumentException(
                    "El stock disponible no puede ser mayor al stock total"
            );
        }

        return inventarioRepository.save(existente);
    }

    // =========================================================
    // ELIMINAR INVENTARIO
    // =========================================================

    @Override
    @Transactional
    public void deleteById(Long id) {

        log.info("Eliminando inventario ID: {}", id);

        if (!inventarioRepository.existsById(id)) {
            throw new RuntimeException(
                    "Inventario no encontrado con ID: " + id
            );
        }

        inventarioRepository.deleteById(id);
    }

    // =========================================================
    // INVENTARIO CON STOCK DISPONIBLE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findConStockDisponible() {

        log.info("Buscando productos con stock disponible");

        return inventarioRepository
                .findByStockDisponibleGreaterThan(0);
    }

    // =========================================================
    // BUSCAR POR PRODUCTO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventario> findByProducto(Long idProducto) {

        log.info(
                "Buscando inventario del producto ID: {}",
                idProducto
        );

        return inventarioRepository
                .findByProductoIdProducto(idProducto);
    }

    // =========================================================
    // STOCK BAJO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findStockBajo() {

        log.info("Buscando productos con stock bajo");

        return inventarioRepository.findStockBajo();
    }

    // =========================================================
    // INVENTARIO POR CATEGORÍA
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findByCategoria(Long idCategoria) {

        log.info(
                "Buscando inventario de la categoría ID: {}",
                idCategoria
        );

        return inventarioRepository.findByCategoria(idCategoria);
    }

    // =========================================================
    // AUMENTAR STOCK
    // =========================================================

    @Override
    @Transactional
    public Inventario aumentarStock(
            Long idProducto,
            Integer cantidad) {

        log.info(
                "Aumentando stock del producto ID: {} en {} unidades",
                idProducto,
                cantidad
        );

        validarCantidad(cantidad);

        Inventario inventario = inventarioRepository
                .findByProductoWithLock(idProducto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventario no encontrado para el producto ID: "
                                        + idProducto
                        )
                );

        inventario.aumentarStock(cantidad);

        return inventarioRepository.save(inventario);
    }

    // =========================================================
    // REDUCIR STOCK
    // =========================================================

    @Override
    @Transactional
    public Inventario reducirStock(
            Long idProducto,
            Integer cantidad) {

        log.info(
                "Reduciendo stock del producto ID: {} en {} unidades",
                idProducto,
                cantidad
        );

        validarCantidad(cantidad);

        Inventario inventario = inventarioRepository
                .findByProductoWithLock(idProducto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventario no encontrado para el producto ID: "
                                        + idProducto
                        )
                );

        if (!inventario.tieneStockDisponible(cantidad)) {

            throw new IllegalStateException(
                    "No hay stock disponible suficiente. "
                            + "Stock actual: "
                            + inventario.getStockDisponible()
                            + ", cantidad solicitada: "
                            + cantidad
            );
        }

        inventario.reducirStock(cantidad);

        return inventarioRepository.save(inventario);
    }

    // =========================================================
    // VERIFICAR DISPONIBILIDAD
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(
            Long idProducto,
            Integer cantidad) {

        validarCantidad(cantidad);

        Integer stockDisponible =
                inventarioRepository
                        .findStockDisponibleByProducto(idProducto);

        return stockDisponible != null
                && stockDisponible >= cantidad;
    }

    // =========================================================
    // VALIDAR CANTIDAD
    // =========================================================

    private void validarCantidad(Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
    }
}
