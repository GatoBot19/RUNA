import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VentaService } from '../../core/services/venta.service';
import { InventarioService } from '../../core/services/inventario.service';
import { ClienteService } from '../../core/services/cliente.service';
import { PedidoService } from '../../core/services/pedido.service';
import { VentaDTO } from '../../core/models/api.models';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private ventaService = inject(VentaService);
  private inventarioService = inject(InventarioService);
  private clienteService = inject(ClienteService);
  private pedidoService = inject(PedidoService);

  ventas = signal<VentaDTO[]>([]);
  totalStockUds = signal<number>(0);
  alertasStockCount = signal<number>(0);
  
  // KPIs
  totalIngresos = computed(() => {
    return this.ventas().reduce((acc, v) => acc + (v.total || 0), 0);
  });

  pedidosActivosCount = computed(() => {
    return this.pedidoService.getAll().filter(p => p.estado !== 'Entregado' && p.estado !== 'Cancelado').length;
  });

  clientesActivosCount = computed(() => {
    return this.clienteService.getAll().filter(c => c.estado === 'Activo').length;
  });

  // Top productos vendidos calculados dinámicamente de las ventas
  topProductos = computed(() => {
    const map: Record<string, number> = {};
    for (const v of this.ventas()) {
      const name = v.nombreProducto || 'Producto';
      map[name] = (map[name] || 0) + (v.cantidad || 0);
    }
    const arr = Object.entries(map).map(([nombre, uds]) => ({ nombre, uds }));
    arr.sort((a, b) => b.uds - a.uds);
    if (arr.length === 0) {
      return [
        { nombre: 'Envase plástico 1L', uds: 120 },
        { nombre: 'Tapa rosca 28mm', uds: 95 },
        { nombre: 'Botella PET 500ml', uds: 78 },
        { nombre: 'Envase cuadrado 2L', uds: 54 },
        { nombre: 'Galonera 5L', uds: 42 }
      ];
    }
    return arr.slice(0, 5);
  });

  ngOnInit(): void {
    this.cargarMetricas();
  }

  cargarMetricas(): void {
    this.ventaService.getAll().subscribe({
      next: (res) => this.ventas.set(res.data || []),
      error: () => {}
    });

    this.inventarioService.getAll().subscribe({
      next: (res) => {
        const items = res.data || [];
        const total = items.reduce((acc, i) => acc + (i.stockDisponible || 0), 0);
        this.totalStockUds.set(total);
        const alertas = items.filter(i => (i.stockDisponible || 0) <= (i.stockMinimo || 10)).length;
        this.alertasStockCount.set(alertas);
      },
      error: () => {}
    });
  }
}
