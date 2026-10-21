import { Injectable, signal } from '@angular/core';
import { PedidoDTO } from '../models/api.models';

const STORAGE_KEY = 'runa_pedidos';

const INITIAL_PEDIDOS: PedidoDTO[] = [
  { id: 'PED-1024', cliente: 'Distribuidora San Juan', productos: 'Envase 1L (x500), Tapa 28mm (x500)', total: 775.00, fecha: '20/10/2026', metodoPago: 'Transferencia BCP', estado: 'Pendiente' },
  { id: 'PED-1023', cliente: 'Embotelladora del Sur', productos: 'Botella PET 500ml (x1,000)', total: 900.00, fecha: '20/10/2026', metodoPago: 'Crédito 15 días', estado: 'En preparación' },
  { id: 'PED-1022', cliente: 'Plásticos Industriales Perú', productos: 'Galonera 5L (x200), Bidón 10L (x100)', total: 1380.00, fecha: '19/10/2026', metodoPago: 'Yape / Plin', estado: 'Enviado' },
  { id: 'PED-1021', cliente: 'Comercializadora Los Andes', productos: 'Envase cuadrado 2L (x300)', total: 600.00, fecha: '18/10/2026', metodoPago: 'Transferencia BBVA', estado: 'Entregado' },
  { id: 'PED-1020', cliente: 'Envasadora del Norte', productos: 'Botella PET 500ml (x400)', total: 360.00, fecha: '17/10/2026', metodoPago: 'Efectivo', estado: 'Cancelado' },
];

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private pedidosList = signal<PedidoDTO[]>(this.loadFromStorage());

  readonly pedidos = this.pedidosList.asReadonly();

  private loadFromStorage(): PedidoDTO[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      this.saveToStorage(INITIAL_PEDIDOS);
      return INITIAL_PEDIDOS;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_PEDIDOS;
    }
  }

  private saveToStorage(pedidos: PedidoDTO[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(pedidos));
  }

  getAll(): PedidoDTO[] {
    return this.pedidosList();
  }

  create(pedido: Omit<PedidoDTO, 'id' | 'fecha'>): PedidoDTO {
    const nextNum = 1025 + this.pedidosList().length;
    const today = new Date();
    const fecha = `${String(today.getDate()).padStart(2, '0')}/${String(today.getMonth() + 1).padStart(2, '0')}/${today.getFullYear()}`;
    const newPedido: PedidoDTO = {
      ...pedido,
      id: `PED-${nextNum}`,
      fecha
    };
    const updated = [newPedido, ...this.pedidosList()];
    this.pedidosList.set(updated);
    this.saveToStorage(updated);
    return newPedido;
  }

  updateEstado(id: string, nuevoEstado: PedidoDTO['estado']): PedidoDTO | null {
    const current = this.pedidosList();
    const index = current.findIndex(p => p.id === id);
    if (index === -1) return null;

    const updated = [...current];
    updated[index] = { ...updated[index], estado: nuevoEstado };
    this.pedidosList.set(updated);
    this.saveToStorage(updated);
    return updated[index];
  }

  delete(id: string): void {
    const updated = this.pedidosList().filter(p => p.id !== id);
    this.pedidosList.set(updated);
    this.saveToStorage(updated);
  }
}
