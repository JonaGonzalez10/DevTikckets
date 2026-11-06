import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Asiento,
  DatosPagoSimulado,
  EventoDetalle,
  Orden
} from '../../models/compra';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private readonly http = inject(HttpClient);

  evento(id: number): Observable<EventoDetalle> {
    return this.http.get<EventoDetalle>(`${environment.apiUrl}/eventos/${id}`);
  }

  asientos(idFuncion: number): Observable<Asiento[]> {
    return this.http.get<Asiento[]>(`${environment.apiUrl}/funciones/${idFuncion}/asientos`);
  }

  reservar(idFuncion: number, idsAsiento: number[]): Observable<Orden> {
    return this.http.post<Orden>(`${environment.apiUrl}/ordenes`, { idFuncion, idsAsiento });
  }

  confirmarPago(idOrden: number, pago: DatosPagoSimulado): Observable<Orden> {
    return this.http.post<Orden>(`${environment.apiUrl}/ordenes/${idOrden}/pago`, { pago });
  }

  misOrdenes(): Observable<Orden[]> {
    return this.http.get<Orden[]>(`${environment.apiUrl}/ordenes`);
  }
}
