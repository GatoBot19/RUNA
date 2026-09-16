package com.runa.runa.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductoDTO {

    private Long idProducto;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private String estado;

    private LocalDateTime fechaCreacion;

    // Información de la categoría
    private Long idCategoria;

    private String nombreCategoria;
}
