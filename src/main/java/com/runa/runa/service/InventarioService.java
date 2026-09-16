package com.runa.runa.service;

import com.runa.runa.model.entity.Inventario;

import java.util.List;
import java.util.Optional;

public interface InventarioService {

    // Operaciones básicas
    List<Inventario> findAll();

    Optional<Inventario> findById(Long id);

    Inventario save(Inventario inventario, Long idProducto);

    Inventario update(Long id, Inventario inventario, Long idProducto);

    void deleteById(Long id);

    // Consultas de inventario
    List<Inventario> findConStockDisponible();

    Optional<Inventario> findByProducto(Long idProducto);

    List<Inventario> findStockBajo();

    List<Inventario> findByCategoria(Long idCategoria);

    // Operaciones de stock
    Inventario aumentarStock(Long idProducto, Integer cantidad);

    Inventario reducirStock(Long idProducto, Integer cantidad);

    // Verificar disponibilidad
    boolean verificarDisponibilidad(Long idProducto, Integer cantidad);
}
