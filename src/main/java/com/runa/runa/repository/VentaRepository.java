
package com.runa.runa.repository;

import com.runa.runa.model.entity.Venta;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // =========================================================
    // 1. BUSCAR VENTAS POR USUARIO
    // =========================================================

    List<Venta> findByUsuarioIdUsuario(Long idUsuario);

    // =========================================================
    // 2. BUSCAR VENTAS POR PRODUCTO
    // =========================================================

    List<Venta> findByProductoIdProducto(Long idProducto);

    // =========================================================
    // 3. BUSCAR VENTAS POR CATEGORIA
    // =========================================================

    List<Venta> findByProductoCategoriaIdCategoria(Long idCategoria);

    // =========================================================
    // 4. BUSCAR VENTAS POR ESTADO
    // =========================================================

    List<Venta> findByEstado(String estado);

    // =========================================================
    // 5. BUSCAR VENTAS POR USUARIO Y ESTADO
    // =========================================================

    List<Venta> findByUsuarioIdUsuarioAndEstado(
            Long idUsuario,
            String estado
    );

    // =========================================================
    // 6. BUSCAR VENTAS POR RANGO DE FECHAS
    // =========================================================

    List<Venta> findByFechaVentaBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // =========================================================
    // 7. BUSCAR VENTAS RECIENTES
    // =========================================================

    @Query("""
        SELECT v
        FROM Venta v
        WHERE v.fechaVenta >= :fecha
        ORDER BY v.fechaVenta DESC
    """)
    List<Venta> findVentasRecientes(
            @Param("fecha") LocalDateTime fecha
    );

    // =========================================================
    // 8. CONTAR VENTAS POR ESTADO
    // =========================================================

    long countByEstado(String estado);

    // =========================================================
    // 9. SUMAR PRODUCTOS VENDIDOS
    // Solo ventas completadas
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(v.cantidad), 0)
        FROM Venta v
        WHERE v.estado = 'completada'
    """)
    Long sumarProductosVendidos();

    // =========================================================
    // 10. SUMAR INGRESOS
    // Excluye ventas canceladas
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE v.estado = 'completada'
    """)
    BigDecimal sumarIngresos();

    // =========================================================
    // 11. ESTADISTICAS MENSUALES
    // Solo ventas completadas
    // =========================================================

    @Query("""
        SELECT
            MONTH(v.fechaVenta),
            COUNT(v),
            COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE YEAR(v.fechaVenta) = :year
          AND v.estado = 'completada'
        GROUP BY MONTH(v.fechaVenta)
        ORDER BY MONTH(v.fechaVenta)
    """)
    List<Object[]> obtenerEstadisticasPorMes(
            @Param("year") int year
    );

    // =========================================================
    // 12. BUSCAR VENTAS POR PRODUCTO Y FECHAS
    // Incluye historial de ventas canceladas
    // =========================================================

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

    // =========================================================
    // 13. BLOQUEO PESIMISTA POR ID DE VENTA
    // Evita cancelaciones simultaneas de una misma venta
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT v
        FROM Venta v
        WHERE v.idVenta = :id
    """)
    Optional<Venta> findByIdWithLock(
            @Param("id") Long id
    );
}
