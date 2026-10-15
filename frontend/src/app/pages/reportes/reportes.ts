import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-reportes',
  templateUrl: './reportes.html',
})
export class Reportes {
  descargar(tipo: string) {
    const urls: Record<string, string> = {
      'ventas-excel': 'http://localhost:8080/api/reportes/ventas/excel',
      'ventas-pdf': 'http://localhost:8080/api/reportes/ventas/pdf',
      'inventario-excel': 'http://localhost:8080/api/reportes/inventario/excel',
    };

    const token = localStorage.getItem('runa_token');
    const url = urls[tipo];

    if (url) {
      fetch(url, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      })
        .then((res) => {
          if (!res.ok) throw new Error('Error al generar el reporte');
          return res.blob();
        })
        .then((blob) => {
          const downloadUrl = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = downloadUrl;
          const extension = tipo.includes('pdf') ? 'pdf' : 'xlsx';
          a.download = `reporte-${tipo}-${new Date().toISOString().slice(0, 10)}.${extension}`;
          document.body.appendChild(a);
          a.click();
          a.remove();
        })
        .catch((err) => {
          console.error(err);
          alert('Hubo un error al generar el reporte. Verifica que el backend esté activo.');
        });
    }
  }
}
