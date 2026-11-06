import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CompraService } from '../../../core/services/compra.service';
import { Asiento, Orden } from '../../../models/compra';
import { MapaAsientos } from '../../../shared/components/mapa-asientos/mapa-asientos';

@Component({
  selector: 'app-mis-entradas',
  imports: [DatePipe, RouterLink, MapaAsientos],
  templateUrl: './mis-entradas.html',
  styleUrl: './mis-entradas.scss'
})
export class MisEntradas {
  private readonly compraService = inject(CompraService);

  protected readonly ordenes = signal<Orden[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal(false);
  protected readonly abierta = signal<number | null>(null);
  protected readonly cargandoMapa = signal(false);
  protected readonly asientos = signal<Asiento[]>([]);
  protected readonly pagadas = computed(() => this.ordenes().filter(o => o.estado === 'PAGADA'));

  constructor() {
    this.compraService.misOrdenes().subscribe({
      next: ordenes => {
        this.ordenes.set(ordenes);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set(true);
        this.cargando.set(false);
      }
    });
  }

  protected alternarMapa(orden: Orden): void {
    if (this.abierta() === orden.id) {
      this.abierta.set(null);
      return;
    }
    this.abierta.set(orden.id);
    this.asientos.set([]);
    this.cargandoMapa.set(true);
    this.compraService.asientos(orden.idFuncion).subscribe({
      next: asientos => {
        this.asientos.set(asientos);
        this.cargandoMapa.set(false);
      },
      error: () => this.cargandoMapa.set(false)
    });
  }

  protected idsPropios(orden: Orden): number[] {
    return orden.entradas.map(e => e.idAsiento);
  }

  protected mapsUrl(recinto: string): string {
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(recinto)}`;
  }

  protected utc(fecha: string): string {
    return fecha.endsWith('Z') ? fecha : `${fecha}Z`;
  }

  protected dinero(monto: number, moneda: string): string {
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: moneda }).format(monto);
  }
}
