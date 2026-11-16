import axios from 'axios'

interface ErrorRespuesta {
  mensaje?: string
  error?: string
  errores?: Record<string, string> | null
}

function esRegistro(valor: unknown): valor is Record<string, unknown> {
  return typeof valor === 'object' && valor !== null
}

function cuerpoError(error: unknown): ErrorRespuesta | null {
  if (!axios.isAxiosError(error) || !esRegistro(error.response?.data)) return null
  return error.response.data as ErrorRespuesta
}

export function extraerErroresCampos(error: unknown): Record<string, string> {
  const errores = cuerpoError(error)?.errores
  return errores && typeof errores === 'object' ? errores : {}
}

export function extraerMensajeError(error: unknown): string {
  if (axios.isAxiosError(error) && !error.response) {
    return 'No fue posible conectar con el servidor. Verifica tu conexión e inténtalo de nuevo.'
  }
  const cuerpo = cuerpoError(error)
  if (cuerpo?.mensaje) return cuerpo.mensaje
  if (cuerpo?.errores) {
    const detalle = Object.values(cuerpo.errores).find((mensaje) => Boolean(mensaje))
    if (detalle) return detalle
  }
  if (cuerpo?.error) return cuerpo.error
  return 'Ocurrió un error. Inténtalo de nuevo.'
}

export const mensajeDeError = extraerMensajeError
