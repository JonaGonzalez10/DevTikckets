import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.scss'
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    apellido: ['', [Validators.required, Validators.maxLength(100)]],
    correoElectronico: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    telefono: ['', Validators.pattern(/^[0-9+() -]{7,30}$/)],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]]
  });

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set(null);

    const v = this.form.getRawValue();
    this.auth
      .registrar({
        ...v,
        nombre: v.nombre.trim(),
        apellido: v.apellido.trim(),
        correoElectronico: v.correoElectronico.trim(),
        telefono: v.telefono.trim() || null
      })
      .subscribe({
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
    if (err.status === 409) return 'El correo ya está registrado';
    return err.error?.error ?? 'Ocurrió un error inesperado';
  }
}
