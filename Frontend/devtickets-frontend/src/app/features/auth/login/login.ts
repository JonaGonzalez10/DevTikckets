import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    correoElectronico: ['', [Validators.required, Validators.email]],
    contrasena: ['', Validators.required]
  });

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    this.auth.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigateByUrl('/');
      },
      error: (err: HttpErrorResponse) => {
        this.cargando.set(false);
        this.error.set(this.mensajeError(err));
      }
    });
  }

  private mensajeError(err: HttpErrorResponse): string {
    if (err.status === 0) return 'No se pudo conectar con el servidor';
    if (err.status === 401) return 'Correo o contraseña incorrectos';
    return err.error?.error ?? 'Ocurrió un error inesperado';
  }
}
