import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  status: string;
  message: string;
  data: {
    token: string;
    type: string;
    usuario: {
      id: number;
      nombre: string;
      apellido: string;
      email: string;
      rol: string;
    };
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials);
  }

  saveToken(token: string): void {
    localStorage.setItem('runa_token', token);
  }

  saveUser(user: any): void {
    localStorage.setItem('runa_user', JSON.stringify(user));
  }

  getToken(): string | null {
    return localStorage.getItem('runa_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('runa_token');
    localStorage.removeItem('runa_user');
  }
}
