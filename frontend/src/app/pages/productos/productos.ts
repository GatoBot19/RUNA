import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../core/services/producto.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { InventarioService } from '../../core/services/inventario.service';
import { ProductoDTO, CategoriaDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-productos',
  imports: [CommonModule, FormsModule],
  templateUrl: './productos.html',
})
export class Productos implements OnInit {
  private productoService = inject(ProductoService);
  private categoriaService = inject(CategoriaService);
  private inventarioService = inject(InventarioService);

  productos = signal<ProductoDTO[]>([]);
  categorias = signal<CategoriaDTO[]>([]);
  stocksMap = signal<Record<number, number>>({});
  loading = signal<boolean>(false);
  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal
  showModal = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentId = signal<number | null>(null);
  
  // Form fields
  formNombre = '';
  formDescripcion = '';
  formPrecio: number = 0;
  formIdCategoria: number = 1;
  formEstado = 'activo';
  formStockInicial: number = 50;
  saving = signal<boolean>(false);

  // KPIs
  totalProductos = computed(() => this.productos().length);
  activosCount = computed(() => this.productos().filter(p => p.estado?.toLowerCase() === 'activo').length);
  inactivosCount = computed(() => this.productos().filter(p => p.estado?.toLowerCase() !== 'activo').length);
  totalCategoriasCount = computed(() => this.categorias().length);

  // Filtrado
  filteredProductos = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.productos();
    return this.productos().filter(p => 
      p.nombre?.toLowerCase().includes(q) ||
      p.descripcion?.toLowerCase().includes(q) ||
      p.nombreCategoria?.toLowerCase().includes(q) ||
      p.idProducto?.toString().includes(q)
    );
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading.set(true);
    
    // Cargar categorías
    this.categoriaService.getAll().subscribe({
      next: (cats) => {
        this.categorias.set(cats || []);
        if (cats && cats.length > 0 && !this.formIdCategoria) {
          this.formIdCategoria = cats[0].idCategoria!;
        }
      },
      error: (e) => console.error('Error al cargar categorías:', e)
    });

    // Cargar inventario para tener el stock actualizado
    this.inventarioService.getAll().subscribe({
      next: (res) => {
        const map: Record<number, number> = {};
        if (res.data) {
          res.data.forEach(inv => {
            if (inv.idProducto) {
              map[inv.idProducto] = inv.stockDisponible ?? inv.stockTotal ?? 0;
            }
          });
        }
        this.stocksMap.set(map);
      },
      error: () => {}
    });

    // Cargar productos
    this.productoService.getAll().subscribe({
      next: (res) => {
        this.productos.set(res.data || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.loading.set(false);
        this.showToast('No se pudieron cargar los productos del servidor', 'error');
      }
    });
  }

  openCreateModal(): void {
    this.isEditing.set(false);
    this.currentId.set(null);
    this.formNombre = '';
    this.formDescripcion = '';
    this.formPrecio = 1.0;
    this.formEstado = 'activo';
    this.formStockInicial = 50;
    if (this.categorias().length > 0) {
      this.formIdCategoria = this.categorias()[0].idCategoria!;
    }
    this.showModal.set(true);
  }

  openEditModal(p: ProductoDTO): void {
    this.isEditing.set(true);
    this.currentId.set(p.idProducto || null);
    this.formNombre = p.nombre;
    this.formDescripcion = p.descripcion || '';
    this.formPrecio = p.precio || 0;
    this.formIdCategoria = p.idCategoria || (this.categorias()[0]?.idCategoria ?? 1);
    this.formEstado = p.estado || 'activo';
    this.formStockInicial = this.stocksMap()[p.idProducto!] || 0;
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  guardar(): void {
    if (!this.formNombre.trim()) {
      this.showToast('El nombre del producto es obligatorio', 'error');
      return;
    }
    if (!this.formPrecio || this.formPrecio <= 0) {
      this.showToast('El precio debe ser mayor a 0', 'error');
      return;
    }
    if (!this.formIdCategoria) {
      this.showToast('Debes seleccionar una categoría', 'error');
      return;
    }

    this.saving.set(true);
    const payload: Partial<ProductoDTO> = {
      nombre: this.formNombre.trim(),
      descripcion: this.formDescripcion.trim(),
      precio: this.formPrecio,
      idCategoria: Number(this.formIdCategoria),
      estado: this.formEstado
    };

    if (this.isEditing() && this.currentId()) {
      this.productoService.update(this.currentId()!, payload).subscribe({
        next: () => {
          this.saving.set(false);
          this.closeModal();
          this.cargarDatos();
          this.showToast('Producto actualizado exitosamente');
        },
        error: (err) => {
          this.saving.set(false);
          this.showToast('Error al actualizar el producto', 'error');
        }
      });
    } else {
      this.productoService.create(payload).subscribe({
        next: (res) => {
          const nuevoProd = res.data;
          // Si se especificó stock inicial, crear registro en inventario
          if (nuevoProd && nuevoProd.idProducto && this.formStockInicial > 0) {
            this.inventarioService.crear(nuevoProd.idProducto, {
              stockTotal: this.formStockInicial,
              stockDisponible: this.formStockInicial,
              stockMinimo: 10
            }).subscribe({
              next: () => {
                this.saving.set(false);
                this.closeModal();
                this.cargarDatos();
                this.showToast('Producto e inventario registrados');
              },
              error: () => {
                this.saving.set(false);
                this.closeModal();
                this.cargarDatos();
                this.showToast('Producto creado con éxito');
              }
            });
          } else {
            this.saving.set(false);
            this.closeModal();
            this.cargarDatos();
            this.showToast('Producto creado con éxito');
          }
        },
        error: (err) => {
          this.saving.set(false);
          this.showToast('Error al crear producto en el backend', 'error');
        }
      });
    }
  }

  toggleEstado(p: ProductoDTO): void {
    if (!p.idProducto) return;
    const esActivo = p.estado?.toLowerCase() === 'activo';
    const action$ = esActivo 
      ? this.productoService.desactivar(p.idProducto) 
      : this.productoService.activar(p.idProducto);

    action$.subscribe({
      next: () => {
        this.showToast(`Producto ${esActivo ? 'desactivado' : 'activado'}`);
        this.cargarDatos();
      },
      error: () => this.showToast('Error al cambiar estado del producto', 'error')
    });
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    if (!confirm('¿Estás seguro de eliminar este producto?')) return;

    this.productoService.delete(id).subscribe({
      next: () => {
        this.cargarDatos();
        this.showToast('Producto eliminado exitosamente');
      },
      error: (err) => {
        this.showToast('No se pudo eliminar el producto (puede tener ventas asociadas)', 'error');
      }
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
