import type { SesionUsuario } from '../modelos/usuario'

const CLAVE_SESION = 'el_cam_sesion'

export function guardarSesion(sesion: SesionUsuario): void {
  localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion))
}

export function leerSesion(): SesionUsuario | null {
  const valor = localStorage.getItem(CLAVE_SESION)
  if (!valor) return null
  try {
    const sesion = JSON.parse(valor) as SesionUsuario
    const expiracion = Date.parse(sesion.expireAt)
    if (!sesion.token || !sesion.expireAt || !Number.isFinite(expiracion) || expiracion <= Date.now()) {
      borrarSesion()
      return null
    }
    return sesion
  } catch {
    borrarSesion()
    return null
  }
}

export function obtenerToken(): string | null {
  return leerSesion()?.token ?? null
}

export function borrarSesion(): void {
  localStorage.removeItem(CLAVE_SESION)
}
