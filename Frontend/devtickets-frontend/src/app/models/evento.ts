export interface EventoResumen {
  id: number;
  nombre: string;
  categoria: string;
  imagenPrincipal: string | null;
  proximaFecha: string | null;
  recinto: string;
  ciudad: string;
  artista: string | null;
  genero: string | null;
  precioMin: number | null;
  precioMax: number | null;
  moneda: string | null;
}
