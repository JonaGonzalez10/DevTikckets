export interface TipoEntrada {
  id: number;
  nombre: string;
  descripcion: string | null;
  precio: number;
  moneda: string;
  disponibles: number;
}

export interface FuncionEvento {
  id: number;
  fechaInicio: string;
  fechaFin: string | null;
  ventaAbierta: boolean;
  recinto: string;
  direccion: string | null;
  ciudad: string | null;
  tiposEntrada: TipoEntrada[];
}

export interface EventoDetalle {
  id: number;
  nombre: string;
  descripcion: string | null;
  categoria: string | null;
  imagenes: { url: string; textoAlternativo: string | null; principal: boolean }[];
  funciones: FuncionEvento[];
}

export interface Asiento {
  id: number;
  seccion: string;
  fila: string;
  numero: number;
  idTipoEntrada: number;
  tipoEntrada: string;
  precio: number;
  moneda: string;
  disponible: boolean;
}

export interface EntradaComprada {
  idAsiento: number;
  codigoEntrada: string;
  seccion: string;
  fila: string;
  numero: number;
  tipoEntrada: string;
  estado: string;
}

export interface Orden {
  id: number;
  numeroOrden: string;
  estado: string;
  idFuncion: number;
  evento: string;
  fechaFuncion: string;
  recinto: string;
  montoTotal: number;
  moneda: string;
  pagadoEn: string | null;
  referenciaPago: string | null;
  entradas: EntradaComprada[];
  expiraEn: string | null;
}

export interface DatosPagoSimulado {
  numeroTarjeta: string;
  titular: string;
  expiracion: string;
  cvv: string;
}
