import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoriaService } from '../../core/services/categoria.service';
import { CategoriaDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-categorias',
  imports: [CommonModule, FormsModule],
  templateUrl: './categorias.html',
})
export class Categorias implements OnInit {
  private categoriaService = inject(CategoriaService);

  categorias = signal<CategoriaDTO[]>([]);
  loading = signal<boolean>(false);
  searchQuery = signal<string>('');
  toastMessage = signal<string | null>(null);
  toastType = signal<'success' | 'error'>('success');

  // Modal
  showModal = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentId = signal<number | null>(null);
  formNombre = '';
  formDescripcion = '';
  saving = signal<boolean>(false);

  // KPIs
  totalCategorias = computed(() => this.categorias().length);
  activasCategorias = computed(() => this.categorias().length);

  // Filtrado
  filteredCategorias = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.categorias();
    return this.categorias().filter(c => 
      c.nombre.toLowerCase().includes(q) || 
      (c.descripcion && c.descripcion.toLowerCase().includes(q)) ||
      c.idCategoria?.toString().includes(q)
    );
  });

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.loading.set(true);
    this.categoriaService.getAll().subscribe({
      next: (data) => {
        this.categorias.set(data || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
        this.loading.set(false);
        this.showToast('No se pudieron cargar las categorías del servidor', 'error');
      }
    });
  }

  openCreateModal(): void {
    this.isEditing.set(false);
    this.currentId.set(null);
    this.formNombre = '';
    this.formDescripcion = '';
    this.showModal.set(true);
  }

  openEditModal(c: CategoriaDTO): void {
    this.isEditing.set(true);
    this.currentId.set(c.idCategoria || null);
    this.formNombre = c.nombre;
    this.formDescripcion = c.descripcion || '';
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  guardar(): void {
    if (!this.formNombre.trim()) {
      this.showToast('El nombre de la categoría es obligatorio', 'error');
      return;
    }

    this.saving.set(true);
    const payload = {
      nombre: this.formNombre.trim(),
      descripcion: this.formDescripcion.trim()
    };

    if (this.isEditing() && this.currentId()) {
      this.categoriaService.update(this.currentId()!, payload).subscribe({
        next: (catActualizada) => {
          this.saving.set(false);
          this.closeModal();
          this.cargarCategorias();
          this.showToast('Categoría actualizada con éxito');
        },
        error: (err) => {
          this.saving.set(false);
          this.showToast('Error al actualizar la categoría', 'error');
        }
      });
    } else {
      this.categoriaService.create(payload).subscribe({
        next: (catNueva) => {
          this.saving.set(false);
          this.closeModal();
          this.cargarCategorias();
          this.showToast('Categoría creada con éxito');
        },
        error: (err) => {
          this.saving.set(false);
          this.showToast('Error al crear la categoría', 'error');
        }
      });
    }
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    if (!confirm('¿Estás seguro de eliminar esta categoría?')) return;

    this.categoriaService.delete(id).subscribe({
      next: () => {
        this.cargarCategorias();
        this.showToast('Categoría eliminada con éxito');
      },
      error: (err) => {
        this.showToast('No se puede eliminar la categoría porque tiene productos asociados o ocurrió un error', 'error');
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
