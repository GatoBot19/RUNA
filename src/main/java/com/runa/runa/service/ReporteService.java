package com.runa.runa.service;

import com.runa.runa.model.entity.Inventario;
import com.runa.runa.model.entity.Venta;
import com.runa.runa.repository.InventarioRepository;
import com.runa.runa.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;

    public ByteArrayInputStream generarReporteVentasExcel() throws IOException {

        List<Venta> ventas = ventaRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Ventas");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ID Venta");
            header.createCell(1).setCellValue("Cliente");
            header.createCell(2).setCellValue("Producto");
            header.createCell(3).setCellValue("Cantidad");
            header.createCell(4).setCellValue("Precio Unitario");
            header.createCell(5).setCellValue("Total");
            header.createCell(6).setCellValue("Estado");
            header.createCell(7).setCellValue("Fecha");

            int rowNum = 1;

            for (Venta venta : ventas) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(
                        venta.getIdVenta() != null
                                ? venta.getIdVenta()
                                : 0
                );

                String cliente = "";

                if (venta.getUsuario() != null) {
                    cliente = venta.getUsuario().getNombre()
                            + " "
                            + venta.getUsuario().getApellido();
                }

                row.createCell(1).setCellValue(cliente);

                row.createCell(2).setCellValue(
                        venta.getProducto() != null
                                ? venta.getProducto().getNombre()
                                : ""
                );

                row.createCell(3).setCellValue(
                        venta.getCantidad() != null
                                ? venta.getCantidad()
                                : 0
                );

                row.createCell(4).setCellValue(
                        venta.getPrecioUnitario() != null
                                ? venta.getPrecioUnitario().doubleValue()
                                : 0
                );

                row.createCell(5).setCellValue(
                        venta.getTotal() != null
                                ? venta.getTotal().doubleValue()
                                : 0
                );

                row.createCell(6).setCellValue(
                        venta.getEstado() != null
                                ? venta.getEstado()
                                : ""
                );

                row.createCell(7).setCellValue(
                        venta.getFechaVenta() != null
                                ? venta.getFechaVenta().toString()
                                : ""
                );
            }

            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(outputStream);

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }

    public ByteArrayInputStream generarReporteInventarioExcel()
            throws IOException {

        List<Inventario> inventarios =
                inventarioRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Inventario");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ID Inventario");
            header.createCell(1).setCellValue("Producto");
            header.createCell(2).setCellValue("Stock Total");
            header.createCell(3).setCellValue("Stock Disponible");
            header.createCell(4).setCellValue("Stock Minimo");
            header.createCell(5).setCellValue("Reposicion");

            int rowNum = 1;

            for (Inventario inventario : inventarios) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(
                        inventario.getIdInventario() != null
                                ? inventario.getIdInventario()
                                : 0
                );

                row.createCell(1).setCellValue(
                        inventario.getProducto() != null
                                ? inventario.getProducto().getNombre()
                                : ""
                );

                row.createCell(2).setCellValue(
                        inventario.getStockTotal() != null
                                ? inventario.getStockTotal()
                                : 0
                );

                row.createCell(3).setCellValue(
                        inventario.getStockDisponible() != null
                                ? inventario.getStockDisponible()
                                : 0
                );

                row.createCell(4).setCellValue(
                        inventario.getStockMinimo() != null
                                ? inventario.getStockMinimo()
                                : 0
                );

                row.createCell(5).setCellValue(
                        inventario.necesitaReposicion()
                                ? "SI"
                                : "NO"
                );
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(outputStream);

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }

    public ByteArrayInputStream generarReporteVentasPDF()
            throws IOException {

        List<Venta> ventas = ventaRepository.findAll();

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);

            document.addPage(page);

            PDType1Font fuenteTitulo =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    );

            PDType1Font fuenteNormal =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    );

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                content.beginText();

                content.setFont(fuenteTitulo, 16);

                content.newLineAtOffset(50, 750);

                content.showText("REPORTE DE VENTAS - RUNA");

                content.setFont(fuenteNormal, 10);

                content.newLineAtOffset(0, -30);

                for (Venta venta : ventas) {

                    String producto =
                            venta.getProducto() != null
                                    ? venta.getProducto().getNombre()
                                    : "Sin producto";

                    BigDecimal total =
                            venta.getTotal() != null
                                    ? venta.getTotal()
                                    : BigDecimal.ZERO;

                    String linea =
                            "Venta #"
                                    + venta.getIdVenta()
                                    + " | "
                                    + producto
                                    + " | Cantidad: "
                                    + venta.getCantidad()
                                    + " | Total: S/ "
                                    + total;

                    content.showText(linea);

                    content.newLineAtOffset(0, -18);
                }

                content.endText();
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            document.save(outputStream);

            return new ByteArrayInputStream(
                    outputStream.toByteArray()
            );
        }
    }
}
