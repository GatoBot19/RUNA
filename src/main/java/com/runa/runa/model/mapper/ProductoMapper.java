package com.runa.runa.model.mapper;

import com.runa.runa.model.dto.ProductoDTO;
import com.runa.runa.model.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    // Entity -> DTO
    @Mapping(source = "categoria.idCategoria", target = "idCategoria")
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    ProductoDTO toDto(Producto producto);

    // List<Entity> -> List<DTO>
    List<ProductoDTO> toDtoList(List<Producto> productos);

    // DTO -> Entity
    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoDTO productoDTO);
}
