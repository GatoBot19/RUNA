import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VentaService } from '../../core/services/venta.service';
import { ProductoService } from '../../core/services/producto.service';
import { VentaDTO, ProductoDTO, ResumenVentas } from '../../core/models/api.models';

@Component({
  selector: 'app-ventas',
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas implements OnInit {
  private ventaService = inject(VentaService);
  private productoService = inject(ProductoService);

  ventas = signal<VentaDTO[]>([]);
  productos = signal<ProductoDTO[]>([]);
  resumen = signal<ResumenVentas | null>(null);
  loading = signal<boolean>(false);
  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal Nueva Venta
  showCreateModal = signal<boolean>(false);
  selectedProductoId: number | null = null;
  selectedProducto: ProductoDTO | null = null;
  cantidad: number = 1;
  saving = signal<boolean>(false);

  // Modal Ver Detalle
  showDetailModal = signal<boolean>(false);
  selectedVenta = signal<VentaDTO | null>(null);

  // KPIs
  totalIngresos = computed(() => {
    return this.ventas().reduce((acc, v) => acc + (v.total || 0), 0);
  });

  totalVentasCount = computed(() => this.ventas().length);
  completadasCount = computed(() => this.ventas().filter(v => v.estado?.toLowerCase() === 'completada').length);
  pendientesCount = computed(() => this.ventas().filter(v => v.estado?.toLowerCase() !== 'completada').length);

  // Filtro
  filteredVentas = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.ventas();
    return this.ventas().filter(v => 
      v.idVenta?.toString().includes(q) ||
      v.nombreProducto?.toLowerCase().includes(q) ||
      v.nombreUsuario?.toLowerCase().includes(q) ||
      v.emailUsuario?.toLowerCase().includes(q) ||
      v.estado?.toLowerCase().includes(q)
    );
  });

  totalCalculado = computed(() => {
    if (!this.selectedProducto || !this.cantidad) return 0;
    return this.cantidad * (this.selectedProducto.precio || 0);
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading.set(true);

    this.productoService.getActivos().subscribe({
      next: (res) => {
        this.productos.set(res.data || []);
      },
      error: () => {}
    });

    this.ventaService.getAll().subscribe({
      next: (res) => {
        this.ventas.set(res.data || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando ventas:', err);
        this.loading.set(false);
        this.showToast('No se pudieron cargar las ventas de la base de datos', 'error');
      }
    });

    this.ventaService.getResumen().subscribe({
      next: (res) => this.resumen.set(res.data),
      error: () => {}
    });
  }

  openCreate(): void {
    if (this.productos().length === 0) {
      this.showToast('No hay productos activos disponibles para vender. Registra uno primero.', 'error');
      return;
    }
    this.selectedProductoId = this.productos()[0].idProducto!;
    this.onProductoChange();
    this.cantidad = 1;
    this.showCreateModal.set(true);
  }

  onProductoChange(): void {
    if (!this.selectedProductoId) {
      this.selectedProducto = null;
      return;
    }
    this.selectedProducto = this.productos().find(p => p.idProducto === Number(this.selectedProductoId)) || null;
  }

  closeCreate(): void {
    this.showCreateModal.set(false);
  }

  crearVenta(): void {
    if (!this.selectedProductoId) {
      this.showToast('Selecciona un producto', 'error');
      return;
    }
    if (!this.cantidad || this.cantidad < 1) {
      this.showToast('La cantidad debe ser al menos 1', 'error');
      return;
    }

    this.saving.set(true);
    this.ventaService.crear({
      idProducto: Number(this.selectedProductoId),
      cantidad: Number(this.cantidad)
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.closeCreate();
        this.cargarDatos();
        this.showToast('¡Venta registrada exitosamente! Stock actualizado.');
      },
      error: (err) => {
        this.saving.set(false);
        const msg = err.error?.message || 'Error al registrar venta. Verifica si hay stock suficiente.';
        this.showToast(msg, 'error');
      }
    });
  }

  verDetalle(v: VentaDTO): void {
    this.selectedVenta.set(v);
    this.showDetailModal.set(true);
  }

  closeDetail(): void {
    this.showDetailModal.set(false);
    this.selectedVenta.set(null);
  }

  cancelarVenta(id: number | undefined): void {
    if (!id) return;
    if (!confirm('¿Deseas cancelar esta venta? El stock será devuelto.')) return;

    this.ventaService.cancelar(id).subscribe({
      next: () => {
        this.cargarDatos();
        this.closeDetail();
        this.showToast('Venta cancelada correctamente');
      },
      error: () => this.showToast('Error al cancelar la venta', 'error')
    });
  }

  eliminarVenta(id: number | undefined): void {
    if (!id) return;
    if (!confirm('¿Estás seguro de eliminar el registro de esta venta?')) return;

    this.ventaService.eliminar(id).subscribe({
      next: () => {
        this.cargarDatos();
        this.closeDetail();
        this.showToast('Venta eliminada de la base de datos');
      },
      error: () => this.showToast('Error al eliminar venta', 'error')
    });
  }

  showToast(msg: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set(msg);
    this.toastType.set(type);
    setTimeout(() => {
      this.toastMessage.set(null);
    }, 3500);
  }
}
