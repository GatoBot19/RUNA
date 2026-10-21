import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
})
export class Layout {
  sidebarOpen = signal(false);
  userName = signal('Administrador');
  userRole = signal('Administrador');
  userInitial = signal('A');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    const raw = localStorage.getItem('runa_user');
    if (raw) {
      try {
        const u = JSON.parse(raw);
        const name = u.nombreCompleto || `${u.nombre || ''} ${u.apellido || ''}`.trim() || u.email || 'Usuario';
        this.userName.set(name);
        this.userRole.set(u.rol || 'ADMIN');
        this.userInitial.set((name[0] || 'U').toUpperCase());
      } catch (e) {}
    }
  }

  toggleSidebar() {
    this.sidebarOpen.update(v => !v);
  }

  closeSidebar() {
    this.sidebarOpen.set(false);
  }

  logout() {
    this.closeSidebar();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
