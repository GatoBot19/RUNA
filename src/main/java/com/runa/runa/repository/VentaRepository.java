package com.runa.runa.repository;

import com.runa.runa.model.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Buscar ventas por usuario
    List<Venta> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar ventas por producto
    List<Venta> findByProductoIdProducto(Long idProducto);

    // Buscar ventas por categoría
    List<Venta> findByProductoCategoriaIdCategoria(Long idCategoria);

    // Buscar ventas por estado
    List<Venta> findByEstado(String estado);

    // Buscar ventas por usuario y estado
    List<Venta> findByUsuarioIdUsuarioAndEstado(
            Long idUsuario,
            String estado
    );

    // Buscar ventas por rango de fechas
    List<Venta> findByFechaVentaBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // Ventas recientes
    @Query("""
        SELECT v
        FROM Venta v
        WHERE v.fechaVenta >= :fecha
        ORDER BY v.fechaVenta DESC
    """)
    List<Venta> findVentasRecientes(
            @Param("fecha") LocalDateTime fecha
    );

    // Total de ventas por estado
    long countByEstado(String estado);

    // Cantidad total de productos vendidos
    @Query("""
        SELECT COALESCE(SUM(v.cantidad), 0)
        FROM Venta v
        WHERE v.estado = 'completada'
    """)
    Long sumarProductosVendidos();

    // Ingresos totales
    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE v.estado = 'completada'
    """)
    java.math.BigDecimal sumarIngresos();

    // Ventas agrupadas por mes
    @Query("""
        SELECT MONTH(v.fechaVenta), COUNT(v), COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE YEAR(v.fechaVenta) = :year
        GROUP BY MONTH(v.fechaVenta)
        ORDER BY MONTH(v.fechaVenta)
    """)
    List<Object[]> obtenerEstadisticasPorMes(
            @Param("year") int year
    );

    // Ventas de un producto dentro de un rango de fechas
    @Query("""
        SELECT v
        FROM Venta v
        WHERE v.producto.idProducto = :idProducto
        AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin
        ORDER BY v.fechaVenta DESC
    """)
    List<Venta> buscarPorProductoYRangoFechas(
            @Param("idProducto") Long idProducto,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}
