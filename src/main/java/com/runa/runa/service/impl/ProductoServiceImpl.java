package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Categoria;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.repository.CategoriaRepository;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.service.ProductoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        log.info("Buscando todos los productos");
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        log.info("Buscando producto por ID: {}", id);
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    public Producto save(Producto producto, Long idCategoria) {

        log.info(
                "Guardando producto: {} - categoría ID: {}",
                producto.getNombre(),
                idCategoria
        );

        if (productoRepository.existsByNombre(producto.getNombre())) {
            throw new RuntimeException(
                    "Ya existe un producto con el nombre: "
                            + producto.getNombre()
            );
        }

        if (idCategoria == null) {
            throw new RuntimeException(
                    "La categoría del producto es obligatoria."
            );
        }

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No existe la categoría con ID: "
                                        + idCategoria
                        )
                );

        producto.setCategoria(categoria);

        if (producto.getEstado() == null ||
                producto.getEstado().isBlank()) {
            producto.setEstado("activo");
        }

        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto update(
            Long id,
            Producto producto,
            Long idCategoria
    ) {

        log.info(
                "Actualizando producto ID: {} - categoría ID: {}",
                id,
                idCategoria
        );

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Producto no encontrado con ID: " + id
                        )
                );

        productoExistente.setNombre(producto.getNombre());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecio(producto.getPrecio());

        if (producto.getEstado() != null &&
                !producto.getEstado().isBlank()) {

            productoExistente.setEstado(producto.getEstado());

        }

        if (idCategoria != null) {

            Categoria categoria = categoriaRepository
                    .findById(idCategoria)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe la categoría con ID: "
                                            + idCategoria
                            )
                    );

            productoExistente.setCategoria(categoria);
        }

        return productoRepository.save(productoExistente);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando producto ID: {}", id);
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByNombreContaining(String nombre) {
        return productoRepository
                .findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByEstado(String estado) {
        return productoRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findActivos() {
        return productoRepository.findByEstadoOrderByNombre("activo");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByPrecioBetween(
            BigDecimal precioMin,
            BigDecimal precioMax
    ) {
        return productoRepository
                .findByPrecioBetween(precioMin, precioMax);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByCategoria(Long idCategoria) {
        return productoRepository
                .findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarConFiltros(
            String nombre,
            BigDecimal precioMin,
            BigDecimal precioMax,
            String estado,
            Long idCategoria
    ) {
        return productoRepository.buscarProductosConFiltros(
                nombre,
                precioMin,
                precioMax,
                estado,
                idCategoria
        );
    }

    @Override
    @Transactional
    public Producto desactivarProducto(Long id) {

        log.info("Desactivando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Producto no encontrado con ID: " + id
                        )
                );

        producto.setEstado("inactivo");

        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto activarProducto(Long id) {

        log.info("Activando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Producto no encontrado con ID: " + id
                        )
                );

        producto.setEstado("activo");

        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return productoRepository.existsByNombre(nombre);
    }
}
