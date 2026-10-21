import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../core/services/proveedor.service';
import { ProveedorDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-proveedores',
  imports: [CommonModule, FormsModule],
  templateUrl: './proveedores.html',
})
export class Proveedores implements OnInit {
  private proveedorService = inject(ProveedorService);

  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal Crear / Editar
  showModal = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentId = signal<string | null>(null);

  formRazonSocial = '';
  formRuc = '';
  formContacto = '';
  formTelefono = '';
  formEmail = '';
  formCategoria = 'Materia Prima (PET/PE)';
  formEstado: 'Activo' | 'Inactivo' = 'Activo';

  // Modal Ficha
  showFichaModal = signal<boolean>(false);
  selectedProveedor = signal<ProveedorDTO | null>(null);

  // Proveedores reactivos
  proveedores = computed(() => this.proveedorService.proveedores());

  // KPIs
  totalProveedores = computed(() => this.proveedores().length);
  activosCount = computed(() => this.proveedores().filter(p => p.estado === 'Activo').length);

  // Filtro de búsqueda
  filteredProveedores = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.proveedores();
    return this.proveedores().filter(p => 
      p.razonSocial.toLowerCase().includes(q) ||
      p.ruc.toLowerCase().includes(q) ||
      p.contacto.toLowerCase().includes(q) ||
      p.email.toLowerCase().includes(q) ||
      p.categoria.toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {}

  openCreateModal(): void {
    this.isEditing.set(false);
    this.currentId.set(null);
    this.formRazonSocial = '';
    this.formRuc = '';
    this.formContacto = '';
    this.formTelefono = '';
    this.formEmail = '';
    this.formCategoria = 'Materia Prima (PET/PE)';
    this.formEstado = 'Activo';
    this.showModal.set(true);
  }

  openEditModal(p: ProveedorDTO): void {
    this.isEditing.set(true);
    this.currentId.set(p.id);
    this.formRazonSocial = p.razonSocial;
    this.formRuc = p.ruc;
    this.formContacto = p.contacto;
    this.formTelefono = p.telefono;
    this.formEmail = p.email;
    this.formCategoria = p.categoria;
    this.formEstado = p.estado;
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  guardar(): void {
    if (!this.formRazonSocial.trim() || !this.formRuc.trim()) {
      this.showToast('La razón social y el RUC son obligatorios', 'error');
      return;
    }

    if (this.isEditing() && this.currentId()) {
      this.proveedorService.update(this.currentId()!, {
        razonSocial: this.formRazonSocial.trim(),
        ruc: this.formRuc.trim(),
        contacto: this.formContacto.trim(),
        telefono: this.formTelefono.trim(),
        email: this.formEmail.trim(),
        categoria: this.formCategoria.trim(),
        estado: this.formEstado
      });
      this.showToast('Proveedor actualizado exitosamente');
    } else {
      this.proveedorService.create({
        razonSocial: this.formRazonSocial.trim(),
        ruc: this.formRuc.trim(),
        contacto: this.formContacto.trim(),
        telefono: this.formTelefono.trim(),
        email: this.formEmail.trim(),
        categoria: this.formCategoria.trim(),
        estado: this.formEstado
      });
      this.showToast('Proveedor registrado exitosamente');
    }

    this.closeModal();
  }

  verFicha(p: ProveedorDTO): void {
    this.selectedProveedor.set(p);
    this.showFichaModal.set(true);
  }

  closeFicha(): void {
    this.showFichaModal.set(false);
    this.selectedProveedor.set(null);
  }

  eliminar(id: string): void {
    if (!confirm('¿Deseas eliminar este proveedor?')) return;
    this.proveedorService.delete(id);
    this.closeFicha();
    this.showToast('Proveedor eliminado del registro');
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
