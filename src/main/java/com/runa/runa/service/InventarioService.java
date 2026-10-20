
package com.runa.runa.service;

import com.runa.runa.model.entity.Inventario;

import java.util.List;
import java.util.Optional;

public interface InventarioService {

    // =========================================================
    // OPERACIONES BASICAS
    // =========================================================

    List<Inventario> findAll();

    Optional<Inventario> findById(Long id);

    Inventario save(Inventario inventario, Long idProducto);

    Inventario update(
            Long id,
            Inventario inventario,
            Long idProducto
    );

    void deleteById(Long id);

    // =========================================================
    // CONSULTAS DE INVENTARIO
    // =========================================================

    List<Inventario> findConStockDisponible();

    Optional<Inventario> findByProducto(Long idProducto);

    List<Inventario> findStockBajo();

    List<Inventario> findByCategoria(Long idCategoria);

    // =========================================================
    // OPERACIONES DE STOCK
    // =========================================================

    Inventario aumentarStock(
            Long idProducto,
            Integer cantidad
    );

    Inventario reducirStock(
            Long idProducto,
            Integer cantidad
    );

    // Devolver productos por cancelacion de una venta
    Inventario devolverStock(
            Long idProducto,
            Integer cantidad
    );

    // =========================================================
    // VERIFICAR DISPONIBILIDAD
    // =========================================================

    boolean verificarDisponibilidad(
            Long idProducto,
            Integer cantidad
    );
}
