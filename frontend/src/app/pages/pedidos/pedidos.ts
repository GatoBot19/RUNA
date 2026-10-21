import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../../core/services/pedido.service';
import { ClienteService } from '../../core/services/cliente.service';
import { PedidoDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-pedidos',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.html',
})
export class Pedidos implements OnInit {
  private pedidoService = inject(PedidoService);
  private clienteService = inject(ClienteService);

  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal Nuevo Pedido
  showCreateModal = signal<boolean>(false);
  formCliente = '';
  formProductos = '';
  formTotal: number = 100;
  formMetodoPago = 'Transferencia BCP';
  formEstado: PedidoDTO['estado'] = 'Pendiente';

  // Modal Guía / Detalle
  showGuiaModal = signal<boolean>(false);
  selectedPedido = signal<PedidoDTO | null>(null);

  // Lista de clientes para el select
  clientes = computed(() => this.clienteService.clientes());
  pedidos = computed(() => this.pedidoService.pedidos());

  // KPIs
  totalPedidos = computed(() => this.pedidos().length);
  porDespachar = computed(() => this.pedidos().filter(p => p.estado === 'Pendiente' || p.estado === 'En preparación').length);
  completados = computed(() => this.pedidos().filter(p => p.estado === 'Entregado' || p.estado === 'Enviado').length);
  cancelados = computed(() => this.pedidos().filter(p => p.estado === 'Cancelado').length);

  // Filtro
  filteredPedidos = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.pedidos();
    return this.pedidos().filter(p => 
      p.id.toLowerCase().includes(q) ||
      p.cliente.toLowerCase().includes(q) ||
      p.productos.toLowerCase().includes(q) ||
      p.estado.toLowerCase().includes(q)
    );
  });

  ngOnInit(): void {
    if (this.clientes().length > 0) {
      this.formCliente = this.clientes()[0].empresa || this.clientes()[0].nombre;
    }
  }

  openCreate(): void {
    if (this.clientes().length > 0) {
      this.formCliente = this.clientes()[0].empresa || this.clientes()[0].nombre;
    } else {
      this.formCliente = 'Cliente Mostrador';
    }
    this.formProductos = 'Envase plástico 1L (x100)';
    this.formTotal = 120.00;
    this.formMetodoPago = 'Transferencia BCP';
    this.formEstado = 'Pendiente';
    this.showCreateModal.set(true);
  }

  closeCreate(): void {
    this.showCreateModal.set(false);
  }

  guardarPedido(): void {
    if (!this.formCliente.trim() || !this.formProductos.trim()) {
      this.showToast('Completa el cliente y productos', 'error');
      return;
    }
    if (!this.formTotal || this.formTotal <= 0) {
      this.showToast('El monto total debe ser mayor a 0', 'error');
      return;
    }

    this.pedidoService.create({
      cliente: this.formCliente.trim(),
      productos: this.formProductos.trim(),
      total: Number(this.formTotal),
      metodoPago: this.formMetodoPago,
      estado: this.formEstado
    });

    this.closeCreate();
    this.showToast('Pedido generado con éxito');
  }

  cambiarEstado(p: PedidoDTO, nuevoEstado: PedidoDTO['estado']): void {
    this.pedidoService.updateEstado(p.id, nuevoEstado);
    this.showToast(`Pedido ${p.id} marcado como "${nuevoEstado}"`);
  }

  verGuia(p: PedidoDTO): void {
    this.selectedPedido.set(p);
    this.showGuiaModal.set(true);
  }

  closeGuia(): void {
    this.showGuiaModal.set(false);
    this.selectedPedido.set(null);
  }

  eliminar(id: string): void {
    if (!confirm('¿Deseas eliminar este pedido del sistema?')) return;
    this.pedidoService.delete(id);
    this.closeGuia();
    this.showToast('Pedido eliminado');
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
