import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = 'http://localhost:8080/api/reportes';

  constructor(private http: HttpClient) {}

  descargarVentasExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/ventas/excel`, { responseType: 'blob' });
  }

  descargarInventarioExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/inventario/excel`, { responseType: 'blob' });
  }

  descargarVentasPDF(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/ventas/pdf`, { responseType: 'blob' });
  }

  guardarArchivo(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
