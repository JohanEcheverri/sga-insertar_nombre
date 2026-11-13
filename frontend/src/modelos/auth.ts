export type Rol = 'HUESPED' | 'RECEPCIONISTA' | 'ADMINISTRADOR' | 'PERSONAL_SERVICIO'
export interface LoginRequest { correo: string; clave: string }
export interface TokenResponse { token: string; tipo?: string; rol?: Rol; correo?: string; expiracion?: string }
