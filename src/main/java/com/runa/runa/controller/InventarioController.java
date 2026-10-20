
package com.runa.runa.controller;

import com.runa.runa.model.dto.InventarioDTO;
import com.runa.runa.model.dto.InventarioUpdateDTO;
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
    // 1. LISTAR TODO EL INVENTARIO
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>>
    getAllInventario() {

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
    // 2. INVENTARIO CON STOCK DISPONIBLE
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
    // 3. BUSCAR INVENTARIO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    getInventarioById(@PathVariable Long id) {

        log.info("GET /api/inventario/{}", id);

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
    // 4. BUSCAR INVENTARIO POR PRODUCTO
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
    // 5. PRODUCTOS CON STOCK BAJO
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
    // 6. INVENTARIO POR CATEGORIA
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
    // 7. CREAR INVENTARIO
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
    // 8. ACTUALIZAR INVENTARIO PARCIALMENTE
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>>
    actualizarInventario(
            @PathVariable Long id,
            @RequestParam(required = false) Long idProducto,
            @RequestBody InventarioUpdateDTO datos) {

        log.info("PUT /api/inventario/{}", id);

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos del inventario son obligatorios"
            );
        }

        // Crear entidad para recibir los cambios.
        Inventario cambios = new Inventario();

        // Los campos omitidos se mantienen en null.
        cambios.setStockTotal(datos.getStockTotal());
        cambios.setStockDisponible(
                datos.getStockDisponible()
        );
        cambios.setStockMinimo(datos.getStockMinimo());

        // Actualizar solo los valores proporcionados.
        Inventario actualizado =
                inventarioService.update(
                        id,
                        cambios,
                        idProducto
                );

        return ResponseEntity.ok(
                ResponseDTO.success(
                        inventarioMapper.toDto(actualizado)
                )
        );
    }

    // =========================================================
    // 9. ELIMINAR INVENTARIO
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
    // 10. AUMENTAR STOCK
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
    // 11. REDUCIR STOCK
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
    // 12. VERIFICAR DISPONIBILIDAD
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
