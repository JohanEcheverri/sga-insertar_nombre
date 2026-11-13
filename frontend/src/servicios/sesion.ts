import type { TokenResponse } from '../modelos/auth'

const CLAVE_SESION = 'sga.sesion'
export function guardarSesion(sesion: TokenResponse): void { localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion)) }
export function leerSesion(): TokenResponse | null {
  const valor = localStorage.getItem(CLAVE_SESION)
  if (!valor) return null
  try { return JSON.parse(valor) as TokenResponse } catch { return null }
}
export function obtenerToken(): string | null { return leerSesion()?.token ?? null }
export function borrarSesion(): void { localStorage.removeItem(CLAVE_SESION) }
