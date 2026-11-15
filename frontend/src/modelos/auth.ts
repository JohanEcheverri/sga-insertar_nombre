export type Rol = 'HUESPED' | 'RECEPCIONISTA' | 'ADMINISTRADOR' | 'PERSONAL_SERVICIO'
export interface LoginRequest { username: string; password: string }
export interface RegistroRequest {
  nombre: string
  apellido: string
  documento: string
  correo: string
  telefono: string
  password: string
  fechaNacimiento: string
}
export interface TokenResponse { token: string; type: 'Bearer'; expireAt: string; roles: Rol[] }
