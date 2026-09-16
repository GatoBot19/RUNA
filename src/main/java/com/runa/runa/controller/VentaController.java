package com.runa.runa.controller;

import com.runa.runa.model.dto.ResponseDTO;
import com.runa.runa.model.dto.VentaDTO;
import com.runa.runa.model.dto.VentaRequest;
import com.runa.runa.model.entity.Producto;
import com.runa.runa.model.entity.Usuario;
import com.runa.runa.model.entity.Venta;
import com.runa.runa.model.mapper.VentaMapper;
import com.runa.runa.repository.ProductoRepository;
import com.runa.runa.service.VentaService;
import com.runa.runa.util.SecurityUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final VentaMapper ventaMapper;
    private final ProductoRepository productoRepository;
    private final SecurityUtil securityUtil;

    // =========================================================
    // CONSULTAS
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getAllVentas() {

        log.info("GET /api/ventas");

        List<VentaDTO> ventas =
                ventaMapper.toDtoList(ventaService.findAll());

        return ResponseEntity.ok(
                ResponseDTO.success(ventas)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<VentaDTO>> getVentaById(
            @PathVariable Long id) {

        log.info("GET /api/ventas/{}", id);

        VentaDTO venta = ventaService.findById(id)
                .map(ventaMapper::toDto)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Venta no encontrada con ID: " + id
                        )
                );

        return ResponseEntity.ok(
                ResponseDTO.success(venta)
        );
    }

    @GetMapping("/mis-ventas")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getMisVentas() {

        Usuario usuario = securityUtil.getCurrentUser()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no autenticado"
                        )
                );

        List<VentaDTO> ventas =
                ventaMapper.toDtoList(
                        ventaService.findByUsuario(
                                usuario.getIdUsuario()
                        )
                );

        return ResponseEntity.ok(
                ResponseDTO.success(ventas)
        );
    }

    // Alias para compatibilidad
    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getMySales() {
        return getMisVentas();
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getVentasByProducto(
            @PathVariable Long idProducto) {

        List<VentaDTO> ventas =
                ventaMapper.toDtoList(
                        ventaService.findByProducto(idProducto)
                );

        return ResponseEntity.ok(
                ResponseDTO.success(ventas)
        );
    }

    @GetMapping("/categoria/{idCategoria}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getVentasByCategoria(
            @PathVariable Long idCategoria) {

        List<VentaDTO> ventas =
                ventaMapper.toDtoList(
                        ventaService.findByCategoria(idCategoria)
                );

        return ResponseEntity.ok(
                ResponseDTO.success(ventas)
        );
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<VentaDTO>>> getVentasByEstado(
            @PathVariable String estado) {

        List<VentaDTO> ventas =
                ventaMapper.toDtoList(
                        ventaService.findByEstado(estado)
                );

        return ResponseEntity.ok(
                ResponseDTO.success(ventas)
        );
    }

    // =========================================================
    // CREAR VENTA
    // =========================================================

    @PostMapping
    public ResponseEntity<ResponseDTO<VentaDTO>> crearVenta(
            @Valid @RequestBody VentaRequest ventaRequest) {

        log.info(
                "POST /api/ventas - Producto: {}, cantidad: {}",
                ventaRequest.getIdProducto(),
                ventaRequest.getCantidad()
        );

        // Usuario autenticado
        Usuario usuario = securityUtil.getCurrentUser()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no autenticado"
                        )
                );

        // Buscar producto
        Producto producto = productoRepository
                .findById(ventaRequest.getIdProducto())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Producto no encontrado con ID: "
                                        + ventaRequest.getIdProducto()
                        )
                );

        // Verificar que el producto esté activo
        if (!"activo".equalsIgnoreCase(producto.getEstado())) {
            throw new IllegalStateException(
                    "El producto seleccionado no está activo"
            );
        }

        /*
         * El precio se obtiene directamente del producto.
         * El frontend NO puede establecer el precio.
         */
        BigDecimal precio = producto.getPrecio();

        if (precio == null) {
            throw new IllegalStateException(
                    "El producto no tiene un precio registrado"
            );
        }

        // Crear venta
        Venta venta = Venta.builder()
                .usuario(usuario)
                .producto(producto)
                .cantidad(ventaRequest.getCantidad())
                .precioUnitario(precio)
                .estado("completada")
                .fechaVenta(LocalDateTime.now())
                .fechaCreacion(LocalDateTime.now())
                .build();

        // Guardar y descontar stock
        Venta ventaGuardada = ventaService.save(venta);

        VentaDTO ventaDTO =
                ventaMapper.toDto(ventaGuardada);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Venta registrada exitosamente",
                        ventaDTO
                )
        );
    }

    // =========================================================
    // CANCELAR VENTA
    // =========================================================

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<VentaDTO>> cancelarVenta(
            @PathVariable Long id) {

        log.info(
                "PUT /api/ventas/{}/cancelar",
                id
        );

        Venta venta =
                ventaService.cancelarVenta(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Venta cancelada exitosamente",
                        ventaMapper.toDto(venta)
                )
        );
    }

    // =========================================================
    // COMPLETAR VENTA
    // =========================================================

    @PutMapping("/{id}/completar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<VentaDTO>> completarVenta(
            @PathVariable Long id) {

        Venta venta =
                ventaService.completarVenta(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Venta completada exitosamente",
                        ventaMapper.toDto(venta)
                )
        );
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<String>> eliminarVenta(
            @PathVariable Long id) {

        ventaService.deleteById(id);

        return ResponseEntity.ok(
                ResponseDTO.success(
                        "Venta eliminada correctamente"
                )
        );
    }

    // =========================================================
    // REPORTES / ESTADÍSTICAS
    // =========================================================

    @GetMapping("/estadisticas/resumen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<ResumenVentas>> obtenerResumen() {

        Long productosVendidos =
                ventaService.sumarProductosVendidos();

        BigDecimal ingresos =
                ventaService.sumarIngresos();

        long ventasCompletadas =
                ventaService.countByEstado("completada");

        ResumenVentas resumen = new ResumenVentas(
                ventasCompletadas,
                productosVendidos,
                ingresos
        );

        return ResponseEntity.ok(
                ResponseDTO.success(resumen)
        );
    }

    @GetMapping("/estadisticas/mensuales/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<Object[]>>>
    obtenerEstadisticasMensuales(
            @PathVariable int year) {

        return ResponseEntity.ok(
                ResponseDTO.success(
                        ventaService.obtenerEstadisticasPorMes(year)
                )
        );
    }

    // =========================================================
    // DTO INTERNO PARA RESUMEN
    // =========================================================

    public record ResumenVentas(
            long ventasCompletadas,
            Long productosVendidos,
            BigDecimal ingresos
    ) {
    }
}
