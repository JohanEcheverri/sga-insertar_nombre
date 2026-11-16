import { useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import type { LoginRequest, RolUsuario, TokenResponse, SesionUsuario } from '../modelos/usuario'
import { borrarSesion, guardarSesion, leerSesion } from '../servicios/sesion'
import { iniciarSesion as autenticar } from '../servicios/authServicio'
import { ContextoAuth } from './authContexto'
import type { AuthContexto } from './authContexto'

function decodificarCorreo(token: string): string {
  try {
    const parte = token.split('.')[1]
    if (!parte) return ''
    const carga = JSON.parse(atob(parte.replace(/-/g, '+').replace(/_/g, '/'))) as { sub?: string; correo?: string }
    return carga.correo ?? carga.sub ?? ''
  } catch {
    return ''
  }
}

function crearSesion(respuesta: TokenResponse): SesionUsuario {
  return { ...respuesta, correo: decodificarCorreo(respuesta.token) }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [sesion, setSesion] = useState<SesionUsuario | null>(() => leerSesion())
  const valor = useMemo<AuthContexto>(() => ({
    sesion,
    estaAutenticado: Boolean(sesion?.token),
    iniciarSesion: async (datos: LoginRequest) => {
      const nuevaSesion = crearSesion(await autenticar(datos))
      guardarSesion(nuevaSesion)
      setSesion(nuevaSesion)
      return nuevaSesion
    },
    cerrarSesion: () => {
      borrarSesion()
      setSesion(null)
    },
    tieneRol: (rolesPermitidos: RolUsuario[]) => Boolean(sesion?.roles.some((rol) => rolesPermitidos.includes(rol))),
  }), [sesion])
  return <ContextoAuth.Provider value={valor}>{children}</ContextoAuth.Provider>
}
