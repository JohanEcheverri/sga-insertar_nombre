import axios from 'axios'
import type { ErrorResponse } from '../modelos/error'

function respuestaError(error: unknown): ErrorResponse | null {
  if (!axios.isAxiosError<ErrorResponse>(error)) return null
  return error.response?.data ?? null
}
export function mensajeDeError(error: unknown): string { return respuestaError(error)?.mensaje ?? (error instanceof Error ? error.message : 'Ocurrió un error inesperado.') }
export function estadoHttp(error: unknown): number | null { return axios.isAxiosError(error) ? error.response?.status ?? null : null }
export function erroresDeCampo(error: unknown): Record<string, string> {
  const errores = respuestaError(error)?.errores ?? {}
  return Object.fromEntries(Object.entries(errores).map(([campo, mensaje]) => [campo.replace(/\[(\d+)\]/g, '.$1'), mensaje]))
}
