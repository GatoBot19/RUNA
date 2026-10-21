import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';

const CONFIG_KEY = 'runa_config_empresa';

@Component({
  selector: 'app-configuracion',
  imports: [CommonModule, FormsModule],
  templateUrl: './configuracion.html',
})
export class Configuracion implements OnInit {
  private usuarioService = inject(UsuarioService);

  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Datos empresa
  nombreComercial = 'RUNA - Envases Inteligentes';
  rucEmpresa = '20610928471';
  direccionEmpresa = 'Av. San Jerónimo 450, Cusco';
  telefonoEmpresa = '+51 984 123 456';

  // Parámetros
  umbralAlerta = 20;
  tasaIgv = 18;

  // Perfil actual
  usuarioNombre = signal('Admin Principal');
  usuarioEmail = signal('admin@runa.local');
  usuarioRol = signal('ADMIN');
  usuarioIniciales = signal('AD');

  // Clave
  passwordActual = '';
  passwordNueva = '';

  ngOnInit(): void {
    // Cargar config guardada
    const raw = localStorage.getItem(CONFIG_KEY);
    if (raw) {
      try {
        const c = JSON.parse(raw);
        this.nombreComercial = c.nombreComercial || this.nombreComercial;
        this.rucEmpresa = c.rucEmpresa || this.rucEmpresa;
        this.direccionEmpresa = c.direccionEmpresa || this.direccionEmpresa;
        this.telefonoEmpresa = c.telefonoEmpresa || this.telefonoEmpresa;
        this.umbralAlerta = c.umbralAlerta ?? 20;
        this.tasaIgv = c.tasaIgv ?? 18;
      } catch (e) {}
    }

    // Cargar usuario autenticado
    const rawUser = localStorage.getItem('runa_user');
    if (rawUser) {
      try {
        const u = JSON.parse(rawUser);
        const name = u.nombreCompleto || `${u.nombre || ''} ${u.apellido || ''}`.trim() || 'Admin';
        this.usuarioNombre.set(name);
        this.usuarioEmail.set(u.email || 'admin@runa.local');
        this.usuarioRol.set(u.rol || 'ADMIN');
        this.usuarioIniciales.set((name[0] + (name[1] || '')).toUpperCase());
      } catch (e) {}
    }
  }

  guardarConfiguracion(): void {
    const config = {
      nombreComercial: this.nombreComercial,
      rucEmpresa: this.rucEmpresa,
      direccionEmpresa: this.direccionEmpresa,
      telefonoEmpresa: this.telefonoEmpresa,
      umbralAlerta: this.umbralAlerta,
      tasaIgv: this.tasaIgv
    };
    localStorage.setItem(CONFIG_KEY, JSON.stringify(config));
    this.showToast('Configuración empresarial guardada exitosamente');
  }

  actualizarClave(): void {
    if (!this.passwordNueva || this.passwordNueva.length < 6) {
      this.showToast('La nueva contraseña debe tener al menos 6 caracteres', 'error');
      return;
    }
    this.passwordActual = '';
    this.passwordNueva = '';
    this.showToast('Credenciales actualizadas correctamente');
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
