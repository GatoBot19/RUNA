package com.runa.runa.service;

import com.runa.runa.model.entity.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> findAll();

    Optional<Producto> findById(Long id);

    Producto save(Producto producto, Long idCategoria);

    Producto update(Long id, Producto producto, Long idCategoria);

    void deleteById(Long id);

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByNombreContaining(String nombre);

    List<Producto> findByEstado(String estado);

    List<Producto> findActivos();

    List<Producto> findByPrecioBetween(
            BigDecimal precioMin,
            BigDecimal precioMax
    );

    List<Producto> findByCategoria(Long idCategoria);

    List<Producto> buscarConFiltros(
            String nombre,
            BigDecimal precioMin,
            BigDecimal precioMax,
            String estado,
            Long idCategoria
    );

    Producto desactivarProducto(Long id);

    Producto activarProducto(Long id);

    boolean existsByNombre(String nombre);
}