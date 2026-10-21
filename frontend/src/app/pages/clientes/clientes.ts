import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../core/services/cliente.service';
import { ClienteDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-clientes',
  imports: [CommonModule, FormsModule],
  templateUrl: './clientes.html',
})
export class Clientes implements OnInit {
  private clienteService = inject(ClienteService);

  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal Crear / Editar
  showModal = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentId = signal<string | null>(null);

  formNombre = '';
  formEmpresa = '';
  formDocumento = '';
  formEmail = '';
  formTelefono = '';
  formDireccion = '';
  formEstado: 'Activo' | 'Inactivo' = 'Activo';

  // Modal Ficha
  showFichaModal = signal<boolean>(false);
  selectedCliente = signal<ClienteDTO | null>(null);

  // Clientes desde el servicio reactivo
  clientes = computed(() => this.clienteService.clientes());

  // KPIs
  carteraTotal = computed(() => this.clientes().length);
  mayoristasCount = computed(() => this.clientes().filter(c => (c.pedidos || 0) >= 15 || c.empresa.toLowerCase().includes('sac') || c.empresa.toLowerCase().includes('srl')).length);
  empresasRucCount = computed(() => this.clientes().filter(c => c.documento?.startsWith('20')).length);
  activosCount = computed(() => this.clientes().filter(c => c.estado === 'Activo').length);

  // Filtro de búsqueda
  filteredClientes = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.clientes();
    return this.clientes().filter(c => 
      c.nombre.toLowerCase().includes(q) ||
      c.empresa.toLowerCase().includes(q) ||
      c.documento.toLowerCase().includes(q) ||
      c.telefono.toLowerCase().includes(q) ||
      c.direccion.toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {}

  openCreateModal(): void {
    this.isEditing.set(false);
    this.currentId.set(null);
    this.formNombre = '';
    this.formEmpresa = '';
    this.formDocumento = '';
    this.formEmail = '';
    this.formTelefono = '';
    this.formDireccion = '';
    this.formEstado = 'Activo';
    this.showModal.set(true);
  }

  openEditModal(c: ClienteDTO): void {
    this.isEditing.set(true);
    this.currentId.set(c.id);
    this.formNombre = c.nombre;
    this.formEmpresa = c.empresa;
    this.formDocumento = c.documento;
    this.formEmail = c.email;
    this.formTelefono = c.telefono;
    this.formDireccion = c.direccion;
    this.formEstado = c.estado;
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  guardar(): void {
    if (!this.formNombre.trim() || !this.formDocumento.trim()) {
      this.showToast('El nombre y el documento son obligatorios', 'error');
      return;
    }

    if (this.isEditing() && this.currentId()) {
      this.clienteService.update(this.currentId()!, {
        nombre: this.formNombre.trim(),
        empresa: this.formEmpresa.trim(),
        documento: this.formDocumento.trim(),
        email: this.formEmail.trim(),
        telefono: this.formTelefono.trim(),
        direccion: this.formDireccion.trim(),
        estado: this.formEstado
      });
      this.showToast('Cliente actualizado con éxito');
    } else {
      this.clienteService.create({
        nombre: this.formNombre.trim(),
        empresa: this.formEmpresa.trim(),
        documento: this.formDocumento.trim(),
        email: this.formEmail.trim(),
        telefono: this.formTelefono.trim(),
        direccion: this.formDireccion.trim(),
        estado: this.formEstado
      });
      this.showToast('Nuevo cliente registrado exitosamente');
    }

    this.closeModal();
  }

  verFicha(c: ClienteDTO): void {
    this.selectedCliente.set(c);
    this.showFichaModal.set(true);
  }

  closeFicha(): void {
    this.showFichaModal.set(false);
    this.selectedCliente.set(null);
  }

  eliminar(id: string): void {
    if (!confirm('¿Deseas eliminar este cliente de la cartera?')) return;
    this.clienteService.delete(id);
    this.closeFicha();
    this.showToast('Cliente eliminado de la cartera');
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
