package com.runa.runa.controller;

import com.runa.runa.model.dto.InventarioDTO;
import com.runa.runa.model.dto.ResponseDTO;
import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.mapper.InventarioMapper;
import com.runa.runa.service.InventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioMapper inventarioMapper;

    // =========================================================
    // LISTAR TODO EL INVENTARIO
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getAllInventario() {

        log.info("GET /api/inventario");

        List<InventarioDTO> inventario =
                inventarioMapper.toDtoList(
                        inventarioService.findAll()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(inventario)
        );
    }

    // =========================================================
    // INVENTARIO CON STOCK DISPONIBLE
    // =========================================================

    @GetMapping("/disponible")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>>
    getInventarioDisponible() {

        log.info("GET /api/inventario/disponible");

        List<InventarioDTO> inventario =
                inventarioMapper.toDtoList(
                        inventarioService.findConStockDisponible()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(inventario)
        );
    }

    // =========================================================
    // BUSCAR INVENTARIO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    getInventarioById(@PathVariable Long id) {

        log.info(
                "GET /api/inventario/{}",
                id
        );

        Inventario inventario =
                inventarioService.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventario no encontrado con ID: "
                                                + id
                                )
                        );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(inventario)
                )
        );
    }

    // =========================================================
    // BUSCAR INVENTARIO POR PRODUCTO
    // =========================================================

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    getInventarioByProducto(
            @PathVariable Long idProducto) {

        log.info(
                "GET /api/inventario/producto/{}",
                idProducto
        );

        Inventario inventario =
                inventarioService.findByProducto(idProducto)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventario no encontrado para el producto ID: "
                                                + idProducto
                                )
                        );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(inventario)
                )
        );
    }

    // =========================================================
    // PRODUCTOS CON STOCK BAJO
    // =========================================================

    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>>
    getStockBajo() {

        log.info("GET /api/inventario/stock-bajo");

        List<InventarioDTO> inventario =
                inventarioMapper.toDtoList(
                        inventarioService.findStockBajo()
                );

        return ResponseEntity.ok(
                ResponseDTO.success(inventario)
        );
    }

    // =========================================================
    // INVENTARIO POR CATEGORÍA
    // =========================================================

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>>
    getInventarioByCategoria(
            @PathVariable Long idCategoria) {

        log.info(
                "GET /api/inventario/categoria/{}",
                idCategoria
        );

        List<InventarioDTO> inventario =
                inventarioMapper.toDtoList(
                        inventarioService.findByCategoria(idCategoria)
                );

        return ResponseEntity.ok(
                ResponseDTO.success(inventario)
        );
    }

    // =========================================================
    // CREAR INVENTARIO
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    crearInventario(
            @RequestParam Long idProducto,
            @RequestBody InventarioDTO inventarioDTO) {

        log.info(
                "POST /api/inventario - producto ID: {}",
                idProducto
        );

        Inventario inventario =
                inventarioMapper.toEntity(inventarioDTO);

        Inventario guardado =
                inventarioService.save(
                        inventario,
                        idProducto
                );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(guardado)
                )
        );
    }

    // =========================================================
    // ACTUALIZAR INVENTARIO
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    actualizarInventario(
            @PathVariable Long id,
            @RequestParam(required = false) Long idProducto,
            @RequestBody InventarioDTO inventarioDTO) {

        log.info(
                "PUT /api/inventario/{}",
                id
        );

        Inventario inventario =
                inventarioMapper.toEntity(inventarioDTO);

        Inventario actualizado =
                inventarioService.update(
                        id,
                        inventario,
                        idProducto
                );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(actualizado)
                )
        );
    }

    // =========================================================
    // ELIMINAR INVENTARIO
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>>
    eliminarInventario(@PathVariable Long id) {

        log.info(
                "DELETE /api/inventario/{}",
                id
        );

        inventarioService.deleteById(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Inventario eliminado correctamente"
                )
        );
    }

    // =========================================================
    // AUMENTAR STOCK
    // =========================================================

    @PutMapping("/producto/{idProducto}/aumentar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    aumentarStock(
            @PathVariable Long idProducto,
            @RequestParam Integer cantidad) {

        log.info(
                "PUT /api/inventario/producto/{}/aumentar?cantidad={}",
                idProducto,
                cantidad
        );

        Inventario inventario =
                inventarioService.aumentarStock(
                        idProducto,
                        cantidad
                );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(inventario)
                )
        );
    }

    // =========================================================
    // REDUCIR STOCK
    // =========================================================

    @PutMapping("/producto/{idProducto}/reducir")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    reducirStock(
            @PathVariable Long idProducto,
            @RequestParam Integer cantidad) {

        log.info(
                "PUT /api/inventario/producto/{}/reducir?cantidad={}",
                idProducto,
                cantidad
        );

        Inventario inventario =
                inventarioService.reducirStock(
                        idProducto,
                        cantidad
                );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(inventario)
                )
        );
    }

    // =========================================================
    // VERIFICAR DISPONIBILIDAD
    // =========================================================

    @GetMapping("/producto/{idProducto}/disponibilidad")
    public ResponseEntity<ResponseDTO<Boolean>>
    verificarDisponibilidad(
            @PathVariable Long idProducto,
            @RequestParam Integer cantidad) {

        log.info(
                "GET /api/inventario/producto/{}/disponibilidad?cantidad={}",
                idProducto,
                cantidad
        );

        boolean disponible =
                inventarioService.verificarDisponibilidad(
                        idProducto,
                        cantidad
                );

        return ResponseEntity.ok(
                ResponseDTO.success(disponible)
        );
    }
}
