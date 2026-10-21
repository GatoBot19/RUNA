import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteService } from '../../core/services/reporte.service';

@Component({
  imports: [CommonModule],
  selector: 'app-reportes',
  templateUrl: './reportes.html',
})
export class Reportes {
  private reporteService = inject(ReporteService);

  descargando = signal<string | null>(null);
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  descargar(tipo: 'ventas-excel' | 'ventas-pdf' | 'inventario-excel'): void {
    this.descargando.set(tipo);

    const dateStr = new Date().toISOString().slice(0, 10);

    if (tipo === 'ventas-excel') {
      this.reporteService.descargarVentasExcel().subscribe({
        next: (blob) => {
          this.reporteService.guardarArchivo(blob, `reporte-ventas-${dateStr}.xlsx`);
          this.descargando.set(null);
          this.showToast('Reporte de ventas en Excel descargado con éxito');
        },
        error: (err) => {
          this.descargando.set(null);
          this.showToast('Error al generar el reporte de ventas en Excel', 'error');
        }
      });
    } else if (tipo === 'ventas-pdf') {
      this.reporteService.descargarVentasPDF().subscribe({
        next: (blob) => {
          this.reporteService.guardarArchivo(blob, `reporte-ventas-${dateStr}.pdf`);
          this.descargando.set(null);
          this.showToast('Reporte oficial de ventas en PDF descargado con éxito');
        },
        error: (err) => {
          this.descargando.set(null);
          this.showToast('Error al generar el reporte PDF', 'error');
        }
      });
    } else if (tipo === 'inventario-excel') {
      this.reporteService.descargarInventarioExcel().subscribe({
        next: (blob) => {
          this.reporteService.guardarArchivo(blob, `reporte-inventario-${dateStr}.xlsx`);
          this.descargando.set(null);
          this.showToast('Balance de inventario en Excel descargado con éxito');
        },
        error: (err) => {
          this.descargando.set(null);
          this.showToast('Error al generar el reporte de inventario', 'error');
        }
      });
    }
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
