package com.runa.runa.model.mapper;

import com.runa.runa.model.dto.VentaDTO;
import com.runa.runa.model.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    VentaMapper INSTANCE = Mappers.getMapper(VentaMapper.class);

    // Entity -> DTO
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "usuario.email", target = "emailUsuario")

    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "producto.categoria.idCategoria", target = "idCategoria")
    @Mapping(source = "producto.categoria.nombre", target = "nombreCategoria")

    VentaDTO toDto(Venta venta);

    // List<Entity> -> List<DTO>
    List<VentaDTO> toDtoList(List<Venta> ventas);
}