import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, HostListener, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EMPTY, catchError, interval, switchMap, tap } from 'rxjs';
import { EventoService } from '../../../core/services/evento.service';
import { CompraService } from '../../../core/services/compra.service';
import { AuthService } from '../../../core/services/auth.service';
import { FiltroCategoriasService } from '../../../core/services/filtro-categorias.service';
import { EventoResumen } from '../../../models/evento';
import { Asiento, EventoDetalle, FuncionEvento, Orden } from '../../../models/compra';
import { MapaAsientos } from '../../../shared/components/mapa-asientos/mapa-asientos';

interface FilaAsientos {
  seccion: string;
  fila: string;
  asientos: Asiento[];
}

@Component({
  selector: 'app-event-list',
  imports: [DatePipe, ReactiveFormsModule, MapaAsientos],
  templateUrl: './event-list.html',
  styleUrl: './event-list.scss'
})
export class EventList {
  private readonly eventoService = inject(EventoService);
  private readonly compraService = inject(CompraService);
  private readonly auth = inject(AuthService);
  private readonly filtro = inject(FiltroCategoriasService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly eventos = signal<EventoResumen[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal(false);
  protected readonly eventoCompra = signal<EventoDetalle | null>(null);
  protected readonly funcionSeleccionada = signal<FuncionEvento | null>(null);
  protected readonly asientos = signal<Asiento[]>([]);
  protected readonly idsSeleccionados = signal<number[]>([]);
  protected readonly orden = signal<Orden | null>(null);
  protected readonly procesando = signal(false);
  protected readonly cargandoCompra = signal(false);
  protected readonly errorCompra = signal<string | null>(null);
  protected readonly ahora = signal(Date.now());
  protected readonly autenticado = this.auth.autenticado;
  protected readonly filas = computed(() => {
    const grupos = new Map<string, FilaAsientos>();
    for (const asiento of this.asientos()) {
      const clave = `${asiento.seccion}\u0000${asiento.fila}`;
      const fila = grupos.get(clave) ?? {
        seccion: asiento.seccion,
        fila: asiento.fila,
        asientos: []
      };
      fila.asientos.push(asiento);
      grupos.set(clave, fila);
    }
    return [...grupos.values()];
  });
  protected readonly zonas = computed(() => {
    const porTipo = new Map<number, { nombre: string; precio: number; moneda: string }>();
    for (const a of this.asientos()) {
      if (!porTipo.has(a.idTipoEntrada)) {
        porTipo.set(a.idTipoEntrada, { nombre: a.tipoEntrada, precio: a.precio, moneda: a.moneda });
      }
    }
    return [...porTipo.values()].sort((a, b) => b.precio - a.precio);
  });
  protected readonly desgloseSeleccion = computed(() => {
    const ids = this.idsSeleccionados();
    const resumen = new Map<string, { tipo: string; cantidad: number; subtotal: number; moneda: string }>();
    for (const a of this.asientos().filter(item => ids.includes(item.id))) {
      const actual = resumen.get(a.tipoEntrada) ?? { tipo: a.tipoEntrada, cantidad: 0, subtotal: 0, moneda: a.moneda };
      actual.cantidad += 1;
      actual.subtotal += a.precio;
      resumen.set(a.tipoEntrada, actual);
    }
    return [...resumen.values()];
  });
  protected readonly totalSeleccionado = computed(() =>
    this.asientos()
      .filter(asiento => this.idsSeleccionados().includes(asiento.id))
      .reduce((total, asiento) => total + asiento.precio, 0)
  );
  protected readonly segundosRestantes = computed(() => {
    const expiraEn = this.orden()?.expiraEn;
    if (!expiraEn) return 0;
    return Math.max(0, Math.ceil((Date.parse(`${expiraEn}Z`) - this.ahora()) / 1000));
  });
  protected readonly pagoForm = this.fb.nonNullable.group({
    numeroTarjeta: ['', [Validators.required, Validators.pattern(/^[0-9]{13,19}$/)]],
    titular: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    expiracion: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
    cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
  });

  constructor() {
    effect(() => {
      const abierto = !!(this.eventoCompra() || this.cargandoCompra() || this.errorCompra());
      document.body.style.overflow = abierto ? 'hidden' : '';
    });
    inject(DestroyRef).onDestroy(() => (document.body.style.overflow = ''));
    interval(1000).pipe(takeUntilDestroyed()).subscribe(() => this.ahora.set(Date.now()));
    toObservable(this.filtro.seleccionadas)
      .pipe(
        tap(() => {
          this.cargando.set(true);
          this.error.set(false);
        }),
        switchMap((ids) =>
          this.eventoService.listarPorCategorias(ids).pipe(
            catchError(() => {
              this.error.set(true);
              this.cargando.set(false);
              return EMPTY;
            })
          )
        )
      )
      .subscribe((data) => {
        this.eventos.set(data);
        this.cargando.set(false);
      });
  }

  protected imagen(ruta: string | null): string {
    return this.eventoService.resolverImagen(ruta);
  }

  protected abrirCompra(evento: EventoResumen): void {
    this.cerrarCompra();
    this.cargandoCompra.set(true);
    this.compraService.evento(evento.id).subscribe({
      next: detalle => {
        this.eventoCompra.set(detalle);
        this.cargandoCompra.set(false);
        const funcionAbierta = detalle.funciones.find(funcion => funcion.ventaAbierta);
        if (funcionAbierta) this.seleccionarFuncion(funcionAbierta.id);
      },
      error: err => {
        this.cargandoCompra.set(false);
        this.errorCompra.set(this.mensajeError(err));
      }
    });
  }

  protected seleccionarFuncion(id: number): void {
    const funcion = this.eventoCompra()?.funciones.find(item => item.id === id) ?? null;
    this.funcionSeleccionada.set(funcion);
    this.idsSeleccionados.set([]);
    this.asientos.set([]);
    this.orden.set(null);
    this.cargandoCompra.set(false);
    if (!funcion) return;

    this.cargandoCompra.set(true);
    this.errorCompra.set(null);
    this.compraService.asientos(funcion.id).subscribe({
      next: asientos => {
        this.asientos.set(asientos);
        this.cargandoCompra.set(false);
      },
      error: err => {
        this.cargandoCompra.set(false);
        this.errorCompra.set(this.mensajeError(err));
      }
    });
  }

  protected cambiarFuncion(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    this.seleccionarFuncion(id);
  }

  protected toggleAsiento(asiento: Asiento): void {
    if (!asiento.disponible || this.orden()) return;
    this.idsSeleccionados.update(ids =>
      ids.includes(asiento.id) ? ids.filter(id => id !== asiento.id) : [...ids, asiento.id]
    );
  }

  protected estaSeleccionado(id: number): boolean {
    return this.idsSeleccionados().includes(id);
  }

  protected reservar(): void {
    const funcion = this.funcionSeleccionada();
    if (!funcion || this.idsSeleccionados().length === 0) return;
    if (!this.auth.token) {
      this.errorCompra.set('Inicia sesión para reservar tus asientos.');
      return;
    }
    this.procesando.set(true);
    this.errorCompra.set(null);
    this.compraService.reservar(funcion.id, this.idsSeleccionados()).subscribe({
      next: orden => {
        this.orden.set(orden);
        this.procesando.set(false);
        this.recargarAsientos(funcion.id);
      },
      error: err => {
        this.procesando.set(false);
        this.errorCompra.set(this.mensajeError(err));
        this.recargarAsientos(funcion.id);
      }
    });
  }

  protected confirmarPago(): void {
    const orden = this.orden();
    if (!orden) return;
    if (this.pagoForm.invalid) {
      this.pagoForm.markAllAsTouched();
      return;
    }
    this.procesando.set(true);
    this.errorCompra.set(null);
    this.compraService.confirmarPago(orden.id, this.pagoForm.getRawValue()).subscribe({
      next: compraPagada => {
        this.orden.set(compraPagada);
        this.procesando.set(false);
        this.idsSeleccionados.set([]);
        const funcionId = this.funcionSeleccionada()?.id;
        if (funcionId) this.recargarAsientos(funcionId);
      },
      error: err => {
        this.procesando.set(false);
        this.errorCompra.set(this.mensajeError(err));
        const funcionId = this.funcionSeleccionada()?.id;
        if (funcionId) this.recargarAsientos(funcionId);
      }
    });
  }

  protected volverAlMapa(): void {
    const funcionId = this.funcionSeleccionada()?.id;
    this.orden.set(null);
    this.idsSeleccionados.set([]);
    this.pagoForm.reset();
    if (funcionId) this.recargarAsientos(funcionId);
  }

  @HostListener('document:keydown.escape')
  protected alEscape(): void {
    if (this.eventoCompra() || this.cargandoCompra() || this.errorCompra()) this.cerrarCompra();
  }

  protected cerrarCompra(): void {
    this.eventoCompra.set(null);
    this.funcionSeleccionada.set(null);
    this.asientos.set([]);
    this.idsSeleccionados.set([]);
    this.orden.set(null);
    this.errorCompra.set(null);
    this.pagoForm.reset();
  }

  protected irAIniciarSesion(): void {
    this.router.navigateByUrl('/login');
  }

  protected irAMisEntradas(): void {
    this.router.navigateByUrl('/mis-entradas');
  }

  protected idsComprados(): number[] {
    return this.orden()?.entradas.map(e => e.idAsiento) ?? [];
  }

  protected mapsUrl(recinto: string, ciudad?: string | null, direccion?: string | null): string {
    const consulta = [recinto, direccion && direccion !== recinto ? direccion : null, ciudad]
      .filter(Boolean)
      .join(', ');
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(consulta)}`;
  }

  protected utc(fecha: string): string {
    return fecha.endsWith('Z') ? fecha : `${fecha}Z`;
  }

  protected rangoPrecios(min: number, max: number, moneda: string): string {
    return min === max
      ? this.dinero(min, moneda)
      : `${this.dinero(min, moneda)} – ${this.dinero(max, moneda)}`;
  }

  protected dinero(monto: number, moneda: string): string {
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: moneda }).format(monto);
  }

  protected tiempoReserva(): string {
    const segundos = this.segundosRestantes();
    return `${Math.floor(segundos / 60).toString().padStart(2, '0')}:${(segundos % 60)
      .toString()
      .padStart(2, '0')}`;
  }

  private recargarAsientos(idFuncion: number): void {
    this.compraService.asientos(idFuncion).subscribe({
      next: asientos => this.asientos.set(asientos),
      error: err => this.errorCompra.set(this.mensajeError(err))
    });
  }

  private mensajeError(err: HttpErrorResponse): string {
    if (err.status === 0) return 'No se pudo conectar con el servidor.';
    return err.error?.error ?? 'Ocurrió un error inesperado.';
  }
}
