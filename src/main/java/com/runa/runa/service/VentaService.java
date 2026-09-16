package com.runa.runa.service;

import com.runa.runa.model.entity.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaService {

    List<Venta> findAll();

    Optional<Venta> findById(Long id);

    Venta save(Venta venta);

    Venta update(Long id, Venta venta);

    void deleteById(Long id);

    List<Venta> findByUsuario(Long idUsuario);

    List<Venta> findByProducto(Long idProducto);

    List<Venta> findByCategoria(Long idCategoria);

    List<Venta> findByEstado(String estado);

    List<Venta> findByUsuarioAndEstado(Long idUsuario, String estado);

    List<Venta> findByRangoFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Venta> findVentasRecientes(LocalDateTime fecha);

    long countByEstado(String estado);

    Long sumarProductosVendidos();

    java.math.BigDecimal sumarIngresos();

    List<Object[]> obtenerEstadisticasPorMes(int year);

    List<Venta> buscarPorProductoYRangoFechas(
            Long idProducto,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    Venta cancelarVenta(Long id);

    Venta completarVenta(Long id);
}
