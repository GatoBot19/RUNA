package com.runa.runa.repository;

import com.runa.runa.model.entity.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // =========================================================
    // BUSCAR INVENTARIO POR PRODUCTO
    // =========================================================

    Optional<Inventario> findByProductoIdProducto(Long idProducto);

    // =========================================================
    // VERIFICAR SI YA EXISTE INVENTARIO PARA UN PRODUCTO
    // =========================================================

    boolean existsByProductoIdProducto(Long idProducto);

    // =========================================================
    // INVENTARIO CON STOCK DISPONIBLE
    // =========================================================

    List<Inventario> findByStockDisponibleGreaterThan(Integer stockMinimo);

    // =========================================================
    // INVENTARIO CON STOCK BAJO
    // =========================================================

    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.stockDisponible <= i.stockMinimo
        ORDER BY i.stockDisponible ASC
    """)
    List<Inventario> findStockBajo();

    // =========================================================
    // INVENTARIO POR CATEGORÍA
    // =========================================================

    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.producto.categoria.idCategoria = :idCategoria
        ORDER BY i.producto.nombre ASC
    """)
    List<Inventario> findByCategoria(
            @Param("idCategoria") Long idCategoria
    );

    // =========================================================
    // VERIFICAR STOCK DISPONIBLE
    // =========================================================

    @Query("""
        SELECT i.stockDisponible
        FROM Inventario i
        WHERE i.producto.idProducto = :idProducto
    """)
    Integer findStockDisponibleByProducto(
            @Param("idProducto") Long idProducto
    );

    // =========================================================
    // BLOQUEO PESIMISTA
    // Evita problemas cuando dos ventas intentan descontar
    // stock del mismo producto al mismo tiempo.
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.idInventario = :id
    """)
    Optional<Inventario> findByIdWithLock(
            @Param("id") Long id
    );

    // =========================================================
    // BLOQUEO PESIMISTA POR PRODUCTO
    // =========================================================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.producto.idProducto = :idProducto
    """)
    Optional<Inventario> findByProductoWithLock(
            @Param("idProducto") Long idProducto
    );
}
