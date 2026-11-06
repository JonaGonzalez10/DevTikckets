import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FiltroCategoriasService {
  readonly seleccionadas = signal<number[]>([]);

  alternar(id: number): void {
    this.seleccionadas.update((ids) => (ids.includes(id) ? ids.filter((i) => i !== id) : [...ids, id]));
  }

  limpiar(): void {
    this.seleccionadas.set([]);
  }
}
