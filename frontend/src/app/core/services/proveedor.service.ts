import { Injectable, signal } from '@angular/core';
import { ProveedorDTO } from '../models/api.models';

const STORAGE_KEY = 'runa_proveedores';

const INITIAL_PROVEEDORES: ProveedorDTO[] = [
  { id: 'PRV-001', razonSocial: 'Petroquímica del Pacífico SA', ruc: '20100123456', contacto: 'Eduardo Vargas', telefono: '981 123 456', email: 'ventas@petroperu.pe', categoria: 'Materia Prima (PET)', estado: 'Activo' },
  { id: 'PRV-002', razonSocial: 'Pigmentos y Colorantes SAC', ruc: '20234567890', contacto: 'Lucía Fernández', telefono: '972 234 567', email: 'contacto@pigmentos.pe', categoria: 'Aditivos y Masterbatch', estado: 'Activo' },
  { id: 'PRV-003', razonSocial: 'Moldes y Matrices del Perú', ruc: '20345678901', contacto: 'Jorge Alva', telefono: '963 345 678', email: 'jalva@moldesperu.com', categoria: 'Maquinaria y Moldes', estado: 'Activo' },
  { id: 'PRV-004', razonSocial: 'Empaques Industriales Andinos', ruc: '20456789012', contacto: 'Patricia Quispe', telefono: '954 456 789', email: 'ventas@empaquesandinos.pe', categoria: 'Cajas y Embalajes', estado: 'Inactivo' },
  { id: 'PRV-005', razonSocial: 'Polímeros Globales SAC', ruc: '20567890123', contacto: 'Fernando Morales', telefono: '945 567 890', email: 'fmorales@polimerosglobal.pe', categoria: 'Materia Prima (PP/PE)', estado: 'Activo' },
];

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {
  private proveedoresList = signal<ProveedorDTO[]>(this.loadFromStorage());

  readonly proveedores = this.proveedoresList.asReadonly();

  private loadFromStorage(): ProveedorDTO[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      this.saveToStorage(INITIAL_PROVEEDORES);
      return INITIAL_PROVEEDORES;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return INITIAL_PROVEEDORES;
    }
  }

  private saveToStorage(proveedores: ProveedorDTO[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(proveedores));
  }

  getAll(): ProveedorDTO[] {
    return this.proveedoresList();
  }

  create(proveedor: Omit<ProveedorDTO, 'id'>): ProveedorDTO {
    const nextNum = this.proveedoresList().length + 1;
    const newProveedor: ProveedorDTO = {
      ...proveedor,
      id: `PRV-${String(nextNum).padStart(3, '0')}`
    };
    const updated = [newProveedor, ...this.proveedoresList()];
    this.proveedoresList.set(updated);
    this.saveToStorage(updated);
    return newProveedor;
  }

  update(id: string, proveedor: Partial<ProveedorDTO>): ProveedorDTO | null {
    const current = this.proveedoresList();
    const index = current.findIndex(p => p.id === id);
    if (index === -1) return null;

    const updatedProveedor = { ...current[index], ...proveedor };
    const updated = [...current];
    updated[index] = updatedProveedor;
    this.proveedoresList.set(updated);
    this.saveToStorage(updated);
    return updatedProveedor;
  }

  delete(id: string): void {
    const updated = this.proveedoresList().filter(p => p.id !== id);
    this.clientesListOrDefault();
    this.proveedoresList.set(updated);
    this.saveToStorage(updated);
  }

  private clientesListOrDefault() {}
}
