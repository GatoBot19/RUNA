package com.runa.runa.controller;

import com.runa.runa.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> descargarVentasExcel()
            throws IOException {

        ByteArrayInputStream in =
                reporteService.generarReporteVentasExcel();

        InputStreamResource file =
                new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ventas-runa.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }

    @GetMapping("/inventario/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> descargarInventarioExcel()
            throws IOException {

        ByteArrayInputStream in =
                reporteService.generarReporteInventarioExcel();

        InputStreamResource file =
                new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=inventario-runa.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }

    @GetMapping("/ventas/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> descargarVentasPDF()
            throws IOException {

        ByteArrayInputStream in =
                reporteService.generarReporteVentasPDF();

        InputStreamResource file =
                new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ventas-runa.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
