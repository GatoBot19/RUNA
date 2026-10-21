import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CategoriaDTO } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private apiUrl = 'http://localhost:8080/api/categorias';

  constructor(private http: HttpClient) {}

  getAll(): Observable<CategoriaDTO[]> {
    return this.http.get<CategoriaDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<CategoriaDTO> {
    return this.http.get<CategoriaDTO>(`${this.apiUrl}/${id}`);
  }

  create(categoria: Partial<CategoriaDTO>): Observable<CategoriaDTO> {
    return this.http.post<CategoriaDTO>(this.apiUrl, categoria);
  }

  update(id: number, categoria: Partial<CategoriaDTO>): Observable<CategoriaDTO> {
    return this.http.put<CategoriaDTO>(`${this.apiUrl}/${id}`, categoria);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
