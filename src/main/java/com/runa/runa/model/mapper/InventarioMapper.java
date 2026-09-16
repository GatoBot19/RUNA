package com.runa.runa.model.mapper;

import com.runa.runa.model.dto.InventarioDTO;
import com.runa.runa.model.entity.Inventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    InventarioMapper INSTANCE = Mappers.getMapper(InventarioMapper.class);

    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "producto.categoria.idCategoria", target = "idCategoria")
    @Mapping(source = "producto.categoria.nombre", target = "nombreCategoria")
    @Mapping(source = "producto.precio", target = "precioProducto")
    @Mapping(
            expression = "java(inventario.necesitaReposicion())",
            target = "necesitaReposicion"
    )
    InventarioDTO toDto(Inventario inventario);

    // =========================================================
    // LISTA ENTITY -> LISTA DTO
    // =========================================================

    List<InventarioDTO> toDtoList(List<Inventario> inventarios);

    // =========================================================
    // DTO -> ENTITY
    // =========================================================

    @Mapping(target = "idInventario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "producto", ignore = true)
    Inventario toEntity(InventarioDTO inventarioDTO);
}
