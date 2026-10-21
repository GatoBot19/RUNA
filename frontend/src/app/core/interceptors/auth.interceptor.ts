import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('runa_token');

  let authReq = req;

  // Agregar token para todas las peticiones excepto login/register
  if (token && !req.url.includes('/api/auth/login') && !req.url.includes('/api/auth/register')) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si el token expiró o no es válido (401), redirigir al login
      if (error.status === 401 && !req.url.includes('/api/auth/login')) {
        localStorage.removeItem('runa_token');
        localStorage.removeItem('runa_user');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
