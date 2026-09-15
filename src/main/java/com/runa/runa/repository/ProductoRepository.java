package com.runa.runa.repository;

import com.runa.runa.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar producto por nombre exacto
    Optional<Producto> findByNombre(String nombre);

    // Buscar productos por nombre
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por estado
    List<Producto> findByEstado(String estado);

    // Buscar productos activos ordenados por nombre
    List<Producto> findByEstadoOrderByNombre(String estado);

    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    // Buscar productos por categoría
    List<Producto> findByCategoriaIdCategoria(Long idCategoria);

    // Buscar productos por nombre y estado
    List<Producto> findByNombreContainingIgnoreCaseAndEstado(
            String nombre,
            String estado
    );

    // Buscar productos con precio menor o igual
    List<Producto> findByPrecioLessThanEqual(BigDecimal precioMax);

    // Buscar productos con precio mayor o igual
    List<Producto> findByPrecioGreaterThanEqual(BigDecimal precioMin);

    // Consulta personalizada para búsqueda avanzada
    @Query("SELECT p FROM Producto p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:precioMin IS NULL OR p.precio >= :precioMin) AND " +
            "(:precioMax IS NULL OR p.precio <= :precioMax) AND " +
            "(:estado IS NULL OR p.estado = :estado) AND " +
            "(:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria)")
    List<Producto> buscarProductosConFiltros(
            @Param("nombre") String nombre,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("estado") String estado,
            @Param("idCategoria") Long idCategoria
    );

    // Verificar si ya existe un producto con ese nombre
    boolean existsByNombre(String nombre);
}