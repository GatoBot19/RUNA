import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;
    this.cdr.detectChanges();

    this.authService.login({ email: this.email, password: this.password })
      .subscribe({
        next: (response) => {
          this.loading = false;
          // Guardar token y datos del usuario
          this.authService.saveToken(response.data.token);
          this.authService.saveUser(response.data.usuario);
          this.cdr.detectChanges();
          // Redirigir al dashboard
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.loading = false;
          if (err.status === 400) {
            this.errorMessage = 'Credenciales incorrectas. Verifica tu email y contraseña.';
          } else if (err.status === 0) {
            this.errorMessage = 'No se puede conectar al servidor. Verifica que el backend esté corriendo.';
          } else {
            this.errorMessage = 'Error al iniciar sesión. Intenta de nuevo.';
          }
          this.cdr.detectChanges();
        }
      });
  }
}
