import { createContext } from 'react'
import type { LoginRequest, RolUsuario, SesionUsuario } from '../modelos/usuario'

export interface AuthContexto {
  sesion: SesionUsuario | null
  estaAutenticado: boolean
  iniciarSesion: (datos: LoginRequest) => Promise<SesionUsuario>
  cerrarSesion: () => void
  tieneRol: (rolesPermitidos: RolUsuario[]) => boolean
}

export const ContextoAuth = createContext<AuthContexto | null>(null)
