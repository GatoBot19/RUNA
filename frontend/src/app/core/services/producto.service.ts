import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseDTO, ProductoDTO } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = 'http://localhost:8080/api/productos';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ResponseDTO<ProductoDTO[]>> {
    return this.http.get<ResponseDTO<ProductoDTO[]>>(this.apiUrl);
  }

  getActivos(): Observable<ResponseDTO<ProductoDTO[]>> {
    return this.http.get<ResponseDTO<ProductoDTO[]>>(`${this.apiUrl}/activos`);
  }

  getById(id: number): Observable<ResponseDTO<ProductoDTO>> {
    return this.http.get<ResponseDTO<ProductoDTO>>(`${this.apiUrl}/${id}`);
  }

  create(producto: Partial<ProductoDTO>): Observable<ResponseDTO<ProductoDTO>> {
    return this.http.post<ResponseDTO<ProductoDTO>>(this.apiUrl, producto);
  }

  update(id: number, producto: Partial<ProductoDTO>): Observable<ResponseDTO<ProductoDTO>> {
    return this.http.put<ResponseDTO<ProductoDTO>>(`${this.apiUrl}/${id}`, producto);
  }

  delete(id: number): Observable<ResponseDTO<void>> {
    return this.http.delete<ResponseDTO<void>>(`${this.apiUrl}/${id}`);
  }

  activar(id: number): Observable<ResponseDTO<ProductoDTO>> {
    return this.http.put<ResponseDTO<ProductoDTO>>(`${this.apiUrl}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<ResponseDTO<ProductoDTO>> {
    return this.http.put<ResponseDTO<ProductoDTO>>(`${this.apiUrl}/${id}/desactivar`, {});
  }
}
