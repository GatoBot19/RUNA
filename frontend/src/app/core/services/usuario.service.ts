import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseDTO, UsuarioDTO, UsuarioRequest } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';
  private authUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ResponseDTO<UsuarioDTO[]>> {
    return this.http.get<ResponseDTO<UsuarioDTO[]>>(this.apiUrl);
  }

  getById(id: number): Observable<ResponseDTO<UsuarioDTO>> {
    return this.http.get<ResponseDTO<UsuarioDTO>>(`${this.apiUrl}/${id}`);
  }

  crear(usuario: UsuarioRequest): Observable<ResponseDTO<UsuarioDTO>> {
    return this.http.post<ResponseDTO<UsuarioDTO>>(this.apiUrl, usuario);
  }

  eliminar(id: number): Observable<ResponseDTO<void>> {
    return this.http.delete<ResponseDTO<void>>(`${this.apiUrl}/${id}`);
  }

  getProfile(): Observable<ResponseDTO<UsuarioDTO>> {
    return this.http.get<ResponseDTO<UsuarioDTO>>(`${this.authUrl}/profile`);
  }
}
