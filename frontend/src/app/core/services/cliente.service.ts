import { Injectable, signal } from '@angular/core';
import { ClienteDTO } from '../models/api.models';

const STORAGE_KEY = 'runa_clientes';

const INITIAL_CLIENTES: ClienteDTO[] = [
  { id: 'CLI-001', nombre: 'Juan Pérez', empresa: 'Distribuidora San Juan SAC', documento: '20512345678', email: 'juan@sanjuan.pe', telefono: '987 654 321', direccion: 'Av. Industrial 123, Lima', pedidos: 24, totalComprado: 12450.00, estado: 'Activo' },
  { id: 'CLI-002', nombre: 'María Rodríguez', empresa: 'Embotelladora del Sur EIRL', documento: '20601234567', email: 'maria@embotelladorasur.pe', telefono: '976 543 210', direccion: 'Jr. Comercio 456, Arequipa', pedidos: 18, totalComprado: 8920.50, estado: 'Activo' },
  { id: 'CLI-003', nombre: 'Carlos Mendoza', empresa: 'Plásticos Industriales Perú', documento: '20456789012', email: 'cmendoza@plasticosperu.com', telefono: '965 432 109', direccion: 'Av. Argentina 789, Callao', pedidos: 42, totalComprado: 34100.00, estado: 'Activo' },
  { id: 'CLI-004', nombre: 'Ana Torres', empresa: 'Comercializadora Los Andes', documento: '10456789012', email: 'atorres@losandes.pe', telefono: '954 321 098', direccion: 'Calle Real 321, Huancayo', pedidos: 5, totalComprado: 2150.00, estado: 'Inactivo' },
  { id: 'CLI-005', nombre: 'Roberto Sánchez', empresa: 'Envasadora del Norte SRL', documento: '20789012345', email: 'rsanchez@envasadoranorte.pe', telefono: '943 210 987', direccion: 'Av. Larco 654, Trujillo', pedidos: 15, totalComprado: 9780.00, estado: 'Activo' },
];

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private clientesList = signal<ClienteDTO[]>(this.loadFromStorage());

  readonly clientes = this.clientesList.asReadonly();

  private loadFromStorage(): ClienteDTO[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      this.saveToStorage(INITIAL_CLIENTES);
      return INITIAL_CLIENTES;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_CLIENTES;
    }
  }

  private saveToStorage(clientes: ClienteDTO[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(clientes));
  }

  getAll(): ClienteDTO[] {
    return this.clientesList();
  }

  create(cliente: Omit<ClienteDTO, 'id' | 'pedidos' | 'totalComprado'>): ClienteDTO {
    const nextNum = this.clientesList().length + 1;
    const newCliente: ClienteDTO = {
      ...cliente,
      id: `CLI-${String(nextNum).padStart(3, '0')}`,
      pedidos: 0,
      totalComprado: 0
    };
    const updated = [newCliente, ...this.clientesList()];
    this.clientesList.set(updated);
    this.saveToStorage(updated);
    return newCliente;
  }

  update(id: string, cliente: Partial<ClienteDTO>): ClienteDTO | null {
    const current = this.clientesList();
    const index = current.findIndex(c => c.id === id);
    if (index === -1) return null;

    const updatedCliente = { ...current[index], ...cliente };
    const updated = [...current];
    updated[index] = updatedCliente;
    this.clientesList.set(updated);
    this.saveToStorage(updated);
    return updatedCliente;
  }

  delete(id: string): void {
    const updated = this.clientesList().filter(c => c.id !== id);
    this.clientesList.set(updated);
    this.saveToStorage(updated);
  }
}
