import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioDTO, UsuarioRequest } from '../../core/models/api.models';

@Component({
  selector: 'app-usuarios',
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.html',
})
export class Usuarios implements OnInit {
  private usuarioService = inject(UsuarioService);

  usuarios = signal<UsuarioDTO[]>([]);
  loading = signal<boolean>(false);
  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal
  showModal = signal<boolean>(false);
  formNombre = '';
  formApellido = '';
  formEmail = '';
  formPassword = '';
  formTelefono = '';
  formDni = '';
  formRol = 'ADMIN';
  saving = signal<boolean>(false);

  // KPIs
  totalUsuarios = computed(() => this.usuarios().length);
  adminsCount = computed(() => this.usuarios().filter(u => u.rol?.toUpperCase() === 'ADMIN').length);
  otrosCount = computed(() => this.usuarios().filter(u => u.rol?.toUpperCase() !== 'ADMIN').length);
  activosCount = computed(() => this.usuarios().filter(u => u.activo !== false).length);

  // Filtrado
  filteredUsuarios = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.usuarios();
    return this.usuarios().filter(u => 
      u.nombre?.toLowerCase().includes(q) ||
      u.apellido?.toLowerCase().includes(q) ||
      u.email?.toLowerCase().includes(q) ||
      u.dni?.toLowerCase().includes(q) ||
      u.rol?.toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.loading.set(true);
    this.usuarioService.getAll().subscribe({
      next: (res) => {
        this.usuarios.set(res.data || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando usuarios:', err);
        this.loading.set(false);
        this.showToast('No se pudieron cargar los usuarios de la base de datos', 'error');
      }
    });
  }

  openCreateModal(): void {
    this.formNombre = '';
    this.formApellido = '';
    this.formEmail = '';
    this.formPassword = '';
    this.formTelefono = '';
    this.formDni = '';
    this.formRol = 'ADMIN';
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  guardar(): void {
    if (!this.formNombre.trim() || !this.formApellido.trim() || !this.formEmail.trim() || !this.formPassword.trim()) {
      this.showToast('Completa los campos obligatorios (*)', 'error');
      return;
    }

    this.saving.set(true);
    const req: UsuarioRequest = {
      nombre: this.formNombre.trim(),
      apellido: this.formApellido.trim(),
      email: this.formEmail.trim(),
      password: this.formPassword.trim(),
      telefono: this.formTelefono.trim(),
      dni: this.formDni.trim(),
      rol: this.formRol
    };

    this.usuarioService.crear(req).subscribe({
      next: () => {
        this.saving.set(false);
        this.closeModal();
        this.cargarUsuarios();
        this.showToast('Usuario creado exitosamente en la base de datos');
      },
      error: (err) => {
        this.saving.set(false);
        const msg = err.error?.message || 'Error al crear usuario. Verifica que el email no esté repetido.';
        this.showToast(msg, 'error');
      }
    });
  }

  eliminar(u: UsuarioDTO): void {
    if (!u.idUsuario) return;
    if (u.email === 'admin@runa.local') {
      this.showToast('No puedes eliminar el usuario Administrador principal', 'error');
      return;
    }
    if (!confirm(`¿Estás seguro de eliminar al usuario ${u.nombre} ${u.apellido}?`)) return;

    this.usuarioService.eliminar(u.idUsuario).subscribe({
      next: () => {
        this.cargarUsuarios();
        this.showToast('Usuario eliminado de la base de datos');
      },
      error: () => this.showToast('Error al eliminar usuario', 'error')
    });
  }

  getInitials(u: UsuarioDTO): string {
    const fn = (u.nombre || 'U')[0];
    const ln = (u.apellido || 'R')[0];
    return (fn + ln).toUpperCase();
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
