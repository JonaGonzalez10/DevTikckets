import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, forkJoin, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EventoResumen } from '../../models/evento';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private readonly http = inject(HttpClient);

  listar(): Observable<EventoResumen[]> {
    return this.http.get<EventoResumen[]>(`${environment.apiUrl}/eventos`);
  }

  // El backend filtra por una sola categoría; con varias se consulta cada una y se unen sin duplicados.
  listarPorCategorias(ids: number[]): Observable<EventoResumen[]> {
    if (ids.length === 0) return this.listar();
    const consultas = ids.map((id) =>
      this.http.get<EventoResumen[]>(`${environment.apiUrl}/eventos`, { params: { categoriaId: id } })
    );
    return forkJoin(consultas).pipe(
      map((listas) => {
        const porId = new Map<number, EventoResumen>();
        listas.flat().forEach((e) => porId.set(e.id, e));
        return [...porId.values()];
      })
    );
  }

  // Las rutas guardadas en BD (p. ej. "assets/eventos/01.png") se sirven desde el frontend.
  resolverImagen(ruta: string | null): string {
    if (!ruta) return '';
    if (/^https?:\/\//i.test(ruta)) return ruta;
    return encodeURI(ruta.startsWith('/') ? ruta : `/${ruta}`);
  }
}
