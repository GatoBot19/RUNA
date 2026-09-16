package com.runa.runa.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventarioDTO {

    private Long idInventario;

    // Información del stock
    private Integer stockTotal;
    private Integer stockDisponible;
    private Integer stockMinimo;

    // Estado del inventario
    private Boolean necesitaReposicion;

    // Fecha de creación
    private LocalDateTime fechaCreacion;

    // Información del producto
    private Long idProducto;
    private String nombreProducto;

    // Información de la categoría
    private Long idCategoria;
    private String nombreCategoria;

    // Precio del producto
    private java.math.BigDecimal precioProducto;
}
