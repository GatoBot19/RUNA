
package com.runa.runa.model.dto;

import lombok.Data;

@Data
public class InventarioUpdateDTO {

    private Integer stockTotal;

    private Integer stockDisponible;

    private Integer stockMinimo;
}
