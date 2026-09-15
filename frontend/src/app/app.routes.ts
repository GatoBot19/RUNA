import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { Layout } from './shared/layout/layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Productos } from './pages/productos/productos';
import { Categorias } from './pages/categorias/categorias';
import { Ventas } from './pages/ventas/ventas';

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
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
