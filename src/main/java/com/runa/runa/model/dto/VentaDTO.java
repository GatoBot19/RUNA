package com.runa.runa.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VentaDTO {

    private Long idVenta;

    // Información del usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String emailUsuario;

    // Información del producto
    private Long idProducto;
    private String nombreProducto;
    private Long idCategoria;
    private String nombreCategoria;

    // Información de la venta
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fechaVenta;
    private LocalDateTime fechaCreacion;
}
