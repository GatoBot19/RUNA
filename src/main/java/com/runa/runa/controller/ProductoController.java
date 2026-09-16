package com.runa.runa.controller;

import com.runa.runa.model.dto.ProductoDTO;
import com.runa.runa.model.dto.ResponseDTO;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.mapper.ProductoMapper;
import com.runa.runa.service.ProductoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>> getAllProductos() {

        log.info("Obteniendo todos los productos");

        List<ProductoDTO> productos =
                productoMapper.toDtoList(
                        productoService.findAll()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(productos)
        );
    }

    @GetMapping("/activos")
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>> getProductosActivos() {

        log.info("Obteniendo productos activos");

        List<ProductoDTO> productos =
                productoMapper.toDtoList(
                        productoService.findActivos()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(productos)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductoDTO>> getProductoById(
            @PathVariable Long id
    ) {

        log.info("Obteniendo producto con ID: {}", id);

        ProductoDTO producto =
                productoService.findById(id)
                        .map(productoMapper::toDto)
                        .orElse(null);

        return ResponseEntity.ok(
                ResponseDTO.success(producto)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<ProductoDTO>> createProducto(
            @RequestBody ProductoDTO productoDTO
    ) {

        log.info(
                "Creando producto: {} - categoría: {}",
                productoDTO.getNombre(),
                productoDTO.getIdCategoria()
        );

        Producto producto =
                productoMapper.toEntity(productoDTO);

        Producto productoGuardado =
                productoService.save(
                        producto,
                        productoDTO.getIdCategoria()
                );

        ProductoDTO productoCreado =
                productoMapper.toDto(productoGuardado);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Producto creado exitosamente",
                        productoCreado
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<ProductoDTO>> updateProducto(
            @PathVariable Long id,
            @RequestBody ProductoDTO productoDTO
    ) {

        log.info(
                "Actualizando producto ID: {} - categoría: {}",
                id,
                productoDTO.getIdCategoria()
        );

        Producto producto =
                productoMapper.toEntity(productoDTO);

        Producto productoActualizado =
                productoService.update(
                        id,
                        producto,
                        productoDTO.getIdCategoria()
                );

        ProductoDTO resultado =
                productoMapper.toDto(productoActualizado);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Producto actualizado exitosamente",
                        resultado
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteProducto(
            @PathVariable Long id
    ) {

        log.info("Eliminando producto ID: {}", id);

        productoService.deleteById(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Producto eliminado exitosamente",
                        null
                )
        );
    }

    @GetMapping("/public/activos")
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>>
    getProductosActivosPublicos() {

        List<ProductoDTO> productos =
                productoMapper.toDtoList(
                        productoService.findActivos()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(productos)
        );
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ResponseDTO<ProductoDTO>>
    getProductoPublico(@PathVariable Long id) {

        ProductoDTO producto =
                productoService.findById(id)
                        .map(productoMapper::toDto)
                        .orElse(null);

        return ResponseEntity.ok(
                ResponseDTO.success(producto)
        );
    }

    @GetMapping("/public/buscar")
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>>
    buscarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Long idCategoria
    ) {

        List<ProductoDTO> productos =
                productoMapper.toDtoList(
                        productoService.buscarConFiltros(
                                nombre,
                                precioMin,
                                precioMax,
                                "activo",
                                idCategoria
                        )
                );

        return ResponseEntity.ok(
                ResponseDTO.success(productos)
        );
    }

    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>>
    searchProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Long idCategoria
    ) {

        return buscarProductos(
                nombre,
                precioMin,
                precioMax,
                idCategoria
        );
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<ResponseDTO<List<ProductoDTO>>>
    getProductosPorCategoria(
            @PathVariable Long idCategoria
    ) {

        List<ProductoDTO> productos =
                productoMapper.toDtoList(
                        productoService.findByCategoria(idCategoria)
                );

        return ResponseEntity.ok(
                ResponseDTO.success(productos)
        );
    }

    @PutMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<ProductoDTO>>
    desactivarProducto(@PathVariable Long id) {

        Producto producto =
                productoService.desactivarProducto(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Producto desactivado exitosamente",
                        productoMapper.toDto(producto)
                )
        );
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<ProductoDTO>>
    activarProducto(@PathVariable Long id) {

        Producto producto =
                productoService.activarProducto(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Producto activado exitosamente",
                        productoMapper.toDto(producto)
                )
        );
    }
}