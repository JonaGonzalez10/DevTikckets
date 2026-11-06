import { Component, computed, input, output } from '@angular/core';
import { Asiento } from '../../../models/compra';

interface FilaAsientos {
  seccion: string;
  fila: string;
  izquierda: Asiento[];
  derecha: Asiento[];
}

@Component({
  selector: 'app-mapa-asientos',
  templateUrl: './mapa-asientos.html',
  styleUrl: './mapa-asientos.scss'
})
export class MapaAsientos {
  readonly asientos = input.required<Asiento[]>();
  readonly propios = input<number[]>([]);
  readonly seleccionados = input<number[]>([]);
  readonly interactivo = input(false);
  readonly elegir = output<Asiento>();

  protected readonly filas = computed(() => {
    const grupos = new Map<string, Asiento[]>();
    for (const asiento of this.asientos()) {
      const clave = `${asiento.seccion}\u0000${asiento.fila}`;
      grupos.set(clave, [...(grupos.get(clave) ?? []), asiento]);
    }
    return [...grupos.values()].map<FilaAsientos>(lista => {
      const ordenados = [...lista].sort((a, b) => a.numero - b.numero);
      const mitad = Math.ceil(ordenados.length / 2);
      return {
        seccion: ordenados[0].seccion,
        fila: ordenados[0].fila,
        izquierda: ordenados.slice(0, mitad),
        derecha: ordenados.slice(mitad)
      };
    });
  });

  protected esPropio(id: number): boolean {
    return this.propios().includes(id);
  }

  protected esSeleccionado(id: number): boolean {
    return this.seleccionados().includes(id);
  }

  protected titulo(asiento: Asiento): string {
    if (this.esPropio(asiento.id)) return 'Tu asiento';
    if (!asiento.disponible) return 'Ocupado';
    const precio = new Intl.NumberFormat('es-MX', { style: 'currency', currency: asiento.moneda }).format(asiento.precio);
    return `${asiento.tipoEntrada} · ${precio}`;
  }

  protected pulsar(asiento: Asiento): void {
    if (this.interactivo() && asiento.disponible) this.elegir.emit(asiento);
  }
}
