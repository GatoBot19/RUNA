import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseDTO, InventarioDTO } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private apiUrl = 'http://localhost:8080/api/inventario';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ResponseDTO<InventarioDTO[]>> {
    return this.http.get<ResponseDTO<InventarioDTO[]>>(this.apiUrl);
  }

  getDisponible(): Observable<ResponseDTO<InventarioDTO[]>> {
    return this.http.get<ResponseDTO<InventarioDTO[]>>(`${this.apiUrl}/disponible`);
  }

  getStockBajo(): Observable<ResponseDTO<InventarioDTO[]>> {
    return this.http.get<ResponseDTO<InventarioDTO[]>>(`${this.apiUrl}/stock-bajo`);
  }

  getById(id: number): Observable<ResponseDTO<InventarioDTO>> {
    return this.http.get<ResponseDTO<InventarioDTO>>(`${this.apiUrl}/${id}`);
  }

  getByProducto(idProducto: number): Observable<ResponseDTO<InventarioDTO>> {
    return this.http.get<ResponseDTO<InventarioDTO>>(`${this.apiUrl}/producto/${idProducto}`);
  }

  crear(idProducto: number, inventario: Partial<InventarioDTO>): Observable<ResponseDTO<InventarioDTO>> {
    const params = new HttpParams().set('idProducto', idProducto.toString());
    return this.http.post<ResponseDTO<InventarioDTO>>(this.apiUrl, inventario, { params });
  }

  actualizar(id: number, idProducto: number, inventario: Partial<InventarioDTO>): Observable<ResponseDTO<InventarioDTO>> {
    let params = new HttpParams();
    if (idProducto) {
      params = params.set('idProducto', idProducto.toString());
    }
    return this.http.put<ResponseDTO<InventarioDTO>>(`${this.apiUrl}/${id}`, inventario, { params });
  }

  eliminar(id: number): Observable<ResponseDTO<string>> {
    return this.http.delete<ResponseDTO<string>>(`${this.apiUrl}/${id}`);
  }

  aumentarStock(idProducto: number, cantidad: number): Observable<ResponseDTO<InventarioDTO>> {
    const params = new HttpParams().set('cantidad', cantidad.toString());
    return this.http.put<ResponseDTO<InventarioDTO>>(`${this.apiUrl}/producto/${idProducto}/aumentar`, {}, { params });
  }

  reducirStock(idProducto: number, cantidad: number): Observable<ResponseDTO<InventarioDTO>> {
    const params = new HttpParams().set('cantidad', cantidad.toString());
    return this.http.put<ResponseDTO<InventarioDTO>>(`${this.apiUrl}/producto/${idProducto}/reducir`, {}, { params });
  }
}
