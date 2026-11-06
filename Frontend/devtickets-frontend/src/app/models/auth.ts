export interface LoginRequest {
  correoElectronico: string;
  contrasena: string;
}

export interface RegistroRequest {
  correoElectronico: string;
  contrasena: string;
  nombre: string;
  apellido: string;
  telefono?: string | null;
}

export interface LoginResponse {
  token: string;
  expiraEnSegundos: number;
  idUsuario: number;
  correoElectronico: string;
  nombre: string;
  apellido: string;
  roles: string[];
}

export interface Sesion extends LoginResponse {
  expiraEn: number;
}
