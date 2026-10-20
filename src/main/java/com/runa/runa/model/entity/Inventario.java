
package com.runa.runa.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    @EqualsAndHashCode.Include
    private Long idInventario;

    // Producto al que pertenece este inventario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Cantidad total registrada
    @Column(name = "stock_total", nullable = false)
    @Builder.Default
    private Integer stockTotal = 0;

    // Cantidad disponible actualmente
    @Column(name = "stock_disponible", nullable = false)
    @Builder.Default
    private Integer stockDisponible = 0;

    // Cantidad mínima antes de generar una alerta de reposición
    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 0;

    // Fecha de creación del registro
    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // =========================================================
    // VERIFICAR STOCK DISPONIBLE
    // =========================================================

    public boolean tieneStockDisponible(Integer cantidad) {

        return cantidad != null
                && cantidad > 0
                && stockDisponible != null
                && stockDisponible >= cantidad;
    }

    // =========================================================
    // REDUCIR STOCK POR VENTA
    // =========================================================

    public void reducirStock(Integer cantidad) {

        validarCantidad(cantidad);

        if (!tieneStockDisponible(cantidad)) {
            throw new IllegalStateException(
                    "No hay stock disponible suficiente"
            );
        }

        this.stockDisponible -= cantidad;
    }

    // =========================================================
    // AUMENTAR STOCK POR REPOSICION
    // =========================================================

    public void aumentarStock(Integer cantidad) {

        validarCantidad(cantidad);

        if (stockDisponible == null || stockTotal == null) {
            throw new IllegalStateException(
                    "Los valores del inventario no pueden ser nulos"
            );
        }

        this.stockDisponible = Math.addExact(
                this.stockDisponible,
                cantidad
        );

        this.stockTotal = Math.addExact(
                this.stockTotal,
                cantidad
        );
    }

    // =========================================================
    // DEVOLVER STOCK POR CANCELACION DE VENTA
    // =========================================================

    public void devolverStock(Integer cantidad) {

        validarCantidad(cantidad);

        if (stockDisponible == null || stockTotal == null) {
            throw new IllegalStateException(
                    "Los valores del inventario no pueden ser nulos"
            );
        }

        if (stockDisponible > stockTotal - cantidad) {
            throw new IllegalStateException(
                    "La devolucion supera el stock total registrado"
            );
        }

        this.stockDisponible += cantidad;
    }

    // =========================================================
    // VERIFICAR STOCK MINIMO
    // =========================================================

    public boolean necesitaReposicion() {

        return stockDisponible != null
                && stockMinimo != null
                && stockDisponible <= stockMinimo;
    }

    // =========================================================
    // VALIDAR CANTIDAD
    // =========================================================

    private void validarCantidad(Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
    }
}
