import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegistroRequest, Sesion } from '../../models/auth';

const CLAVE_SESION = 'devtickets.sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly _sesion = signal<Sesion | null>(this.cargarSesion());

  readonly sesion = this._sesion.asReadonly();
  readonly autenticado = computed(() => this._sesion() !== null);

  get token(): string | null {
    const s = this._sesion();
    if (!s) return null;
    if (Date.now() >= s.expiraEn) {
      this.logout();
      return null;
    }
    return s.token;
  }

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, datos)
      .pipe(tap(res => this.guardarSesion(res)));
  }

  registrar(datos: RegistroRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/registro`, datos)
      .pipe(tap(res => this.guardarSesion(res)));
  }

  private guardarSesion(res: LoginResponse): void {
    const sesion: Sesion = { ...res, expiraEn: Date.now() + res.expiraEnSegundos * 1000 };
    localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion));
    this._sesion.set(sesion);
  }

  logout(): void {
    localStorage.removeItem(CLAVE_SESION);
    this._sesion.set(null);
  }

  private cargarSesion(): Sesion | null {
    try {
      const raw = localStorage.getItem(CLAVE_SESION);
      if (!raw) return null;
      const s = JSON.parse(raw) as Sesion;
      return Date.now() < s.expiraEn ? s : null;
    } catch {
      return null;
    }
  }
}
