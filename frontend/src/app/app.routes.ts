import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { Layout } from './shared/layout/layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Productos } from './pages/productos/productos';
import { Categorias } from './pages/categorias/categorias';
import { Ventas } from './pages/ventas/ventas';
import { Inventario } from './pages/inventario/inventario';
import { Reportes } from './pages/reportes/reportes';
import { Usuarios } from './pages/usuarios/usuarios';
import { Pedidos } from './pages/pedidos/pedidos';
import { Clientes } from './pages/clientes/clientes';
import { Proveedores } from './pages/proveedores/proveedores';
import { Configuracion } from './pages/configuracion/configuracion';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: Layout,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'productos', component: Productos },
      { path: 'categorias', component: Categorias },
      { path: 'ventas', component: Ventas },
      { path: 'inventario', component: Inventario },
      { path: 'pedidos', component: Pedidos },
      { path: 'clientes', component: Clientes },
      { path: 'proveedores', component: Proveedores },
      { path: 'reportes', component: Reportes },
      { path: 'usuarios', component: Usuarios },
      { path: 'configuracion', component: Configuracion },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
