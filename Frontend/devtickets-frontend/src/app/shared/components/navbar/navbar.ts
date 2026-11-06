import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  cerrarSesion(): void {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}
