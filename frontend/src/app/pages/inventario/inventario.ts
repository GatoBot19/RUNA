import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService } from '../../core/services/inventario.service';
import { ProductoService } from '../../core/services/producto.service';
import { InventarioDTO, ProductoDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-inventario',
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
})
export class Inventario implements OnInit {
  private inventarioService = inject(InventarioService);
  private productoService = inject(ProductoService);

  inventarioList = signal<InventarioDTO[]>([]);
  productos = signal<ProductoDTO[]>([]);
  loading = signal<boolean>(false);
  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modales
  showAdjustModal = signal<boolean>(false);
  adjustMode = signal<'aumentar' | 'reducir'>('aumentar');
  selectedItem = signal<InventarioDTO | null>(null);
  selectedProductoId: number | null = null;
  adjustCantidad: number = 10;
  saving = signal<boolean>(false);

  // Modal Nuevo Registro
  showNuevoModal = signal<boolean>(false);
  nuevoProductoId: number | null = null;
  nuevoStockTotal: number = 50;
  nuevoStockMinimo: number = 10;

  // KPIs
  totalEnStock = computed(() => {
    return this.inventarioList().reduce((acc, item) => acc + (item.stockDisponible || 0), 0);
  });

  stockOptimoCount = computed(() => {
    return this.inventarioList().filter(i => (i.stockDisponible || 0) > (i.stockMinimo || 10)).length;
  });

  stockBajoCount = computed(() => {
    return this.inventarioList().filter(i => (i.stockDisponible || 0) <= (i.stockMinimo || 10)).length;
  });

  totalItemsCount = computed(() => this.inventarioList().length);

  // Filtro
  filteredInventario = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.inventarioList();
    return this.inventarioList().filter(item => 
      item.nombreProducto?.toLowerCase().includes(q) ||
      item.nombreCategoria?.toLowerCase().includes(q) ||
      item.idProducto?.toString().includes(q) ||
      item.idInventario?.toString().includes(q)
    );
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading.set(true);

    this.productoService.getAll().subscribe({
      next: (res) => this.productos.set(res.data || []),
      error: () => {}
    });

    this.inventarioService.getAll().subscribe({
      next: (res) => {
        this.inventarioList.set(res.data || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando inventario:', err);
        this.loading.set(false);
        this.showToast('No se pudo cargar el inventario del servidor', 'error');
      }
    });
  }

  openAdjust(mode: 'aumentar' | 'reducir', item?: InventarioDTO): void {
    this.adjustMode.set(mode);
    this.adjustCantidad = 10;

    if (item) {
      this.selectedItem.set(item);
      this.selectedProductoId = item.idProducto;
    } else if (this.inventarioList().length > 0) {
      this.selectedItem.set(this.inventarioList()[0]);
      this.selectedProductoId = this.inventarioList()[0].idProducto;
    } else if (this.productos().length > 0) {
      this.selectedItem.set(null);
      this.selectedProductoId = this.productos()[0].idProducto!;
    } else {
      this.showToast('No hay productos disponibles para ajustar inventario', 'error');
      return;
    }

    this.showAdjustModal.set(true);
  }

  closeAdjust(): void {
    this.showAdjustModal.set(false);
    this.selectedItem.set(null);
  }

  ejecutarAjuste(): void {
    if (!this.selectedProductoId) {
      this.showToast('Selecciona un producto', 'error');
      return;
    }
    if (!this.adjustCantidad || this.adjustCantidad <= 0) {
      this.showToast('La cantidad debe ser mayor a 0', 'error');
      return;
    }

    this.saving.set(true);
    const prodId = Number(this.selectedProductoId);
    const cant = Number(this.adjustCantidad);

    const action$ = this.adjustMode() === 'aumentar'
      ? this.inventarioService.aumentarStock(prodId, cant)
      : this.inventarioService.reducirStock(prodId, cant);

    action$.subscribe({
      next: () => {
        this.saving.set(false);
        this.closeAdjust();
        this.cargarDatos();
        this.showToast(`Stock ${this.adjustMode() === 'aumentar' ? 'incrementado' : 'reducido'} correctamente`);
      },
      error: (err) => {
        this.saving.set(false);
        const msg = err.error?.message || 'Error al actualizar stock en el backend';
        this.showToast(msg, 'error');
      }
    });
  }

  openNuevo(): void {
    if (this.productos().length === 0) {
      this.showToast('Primero crea productos en el módulo Productos', 'error');
      return;
    }
    this.nuevoProductoId = this.productos()[0].idProducto!;
    this.nuevoStockTotal = 50;
    this.nuevoStockMinimo = 10;
    this.showNuevoModal.set(true);
  }

  closeNuevo(): void {
    this.showNuevoModal.set(false);
  }

  guardarNuevo(): void {
    if (!this.nuevoProductoId) {
      this.showToast('Selecciona un producto', 'error');
      return;
    }

    this.saving.set(true);
    this.inventarioService.crear(Number(this.nuevoProductoId), {
      stockTotal: Number(this.nuevoStockTotal),
      stockDisponible: Number(this.nuevoStockTotal),
      stockMinimo: Number(this.nuevoStockMinimo)
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.closeNuevo();
        this.cargarDatos();
        this.showToast('Registro de inventario creado con éxito');
      },
      error: (err) => {
        this.saving.set(false);
        const msg = err.error?.message || 'Error al registrar inventario (puede que ya exista para este producto)';
        this.showToast(msg, 'error');
      }
    });
  }

  getPorcentajeStock(disponible: number = 0, minimo: number = 10): number {
    const meta = Math.max(minimo * 3, 50);
    return Math.min(100, Math.round((disponible / meta) * 100));
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
