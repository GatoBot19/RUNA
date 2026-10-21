import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseDTO, VentaDTO, VentaRequest, ResumenVentas } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  private apiUrl = 'http://localhost:8080/api/ventas';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ResponseDTO<VentaDTO[]>> {
    return this.http.get<ResponseDTO<VentaDTO[]>>(this.apiUrl);
  }

  getById(id: number): Observable<ResponseDTO<VentaDTO>> {
    return this.http.get<ResponseDTO<VentaDTO>>(`${this.apiUrl}/${id}`);
  }

  getMisVentas(): Observable<ResponseDTO<VentaDTO[]>> {
    return this.http.get<ResponseDTO<VentaDTO[]>>(`${this.apiUrl}/mis-ventas`);
  }

  crear(request: VentaRequest): Observable<ResponseDTO<VentaDTO>> {
    return this.http.post<ResponseDTO<VentaDTO>>(this.apiUrl, request);
  }

  cancelar(id: number): Observable<ResponseDTO<VentaDTO>> {
    return this.http.put<ResponseDTO<VentaDTO>>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  completar(id: number): Observable<ResponseDTO<VentaDTO>> {
    return this.http.put<ResponseDTO<VentaDTO>>(`${this.apiUrl}/${id}/completar`, {});
  }

  eliminar(id: number): Observable<ResponseDTO<string>> {
    return this.http.delete<ResponseDTO<string>>(`${this.apiUrl}/${id}`);
  }

  getResumen(): Observable<ResponseDTO<ResumenVentas>> {
    return this.http.get<ResponseDTO<ResumenVentas>>(`${this.apiUrl}/estadisticas/resumen`);
  }

  getEstadisticasMensuales(year: number): Observable<ResponseDTO<any[]>> {
    return this.http.get<ResponseDTO<any[]>>(`${this.apiUrl}/estadisticas/mensuales/${year}`);
  }
}
