import type { Pagina } from '../modelos/comun'
import type { CrearReservaRequest, FiltrosReservas, ReservaDetalle, ReservaResumen } from '../modelos/reserva'
import { api } from './api'

export async function crearReserva(datos: CrearReservaRequest): Promise<ReservaDetalle> {
  return (await api.post<ReservaDetalle>('/reservas', datos)).data
}

export async function obtenerReserva(codigo: string): Promise<ReservaDetalle> {
  return (await api.get<ReservaDetalle>(`/reservas/${codigo}`)).data
}

/** Historial del huésped autenticado (GET /usuarios/{id}/reservas, más recientes primero). */
export async function listarMisReservas(usuarioId: string): Promise<ReservaResumen[]> {
  return (await api.get<ReservaResumen[]>(`/usuarios/${usuarioId}/reservas`)).data
}

async function accion(codigo: string, ruta: string): Promise<ReservaDetalle> {
  return (await api.put<ReservaDetalle>(`/reservas/${codigo}/${ruta}`)).data
}
export const cancelarReserva = (codigo: string) => accion(codigo, 'cancelar')
export const confirmarReserva = (codigo: string) => accion(codigo, 'confirmar')
export const registrarLlegada = (codigo: string) => accion(codigo, 'registrar-llegada')
export const registrarSalida = (codigo: string) => accion(codigo, 'registrar-salida')
export const declararNoShow = (codigo: string) => accion(codigo, 'no-show')

export async function listarReservas(filtros: FiltrosReservas, pagina = 0): Promise<Pagina<ReservaResumen>> {
  const params: Record<string, string | number> = { pagina }
  for (const [clave, valor] of Object.entries(filtros)) if (valor) params[clave] = valor
  return (await api.get<Pagina<ReservaResumen>>('/reservas', { params })).data
}

export async function llegadasDelDia(fecha: string, pagina = 0): Promise<Pagina<ReservaResumen>> {
  return (await api.get<Pagina<ReservaResumen>>('/reservas/llegadas', { params: { fecha, pagina } })).data
}

export async function salidasDelDia(fecha: string, pagina = 0): Promise<Pagina<ReservaResumen>> {
  return (await api.get<Pagina<ReservaResumen>>('/reservas/salidas', { params: { fecha, pagina } })).data
}

/** Total de reservas de un canal (incluye canceladas y no-show) leyendo totalElementos. */
export async function contarReservasPorCanal(canal: string): Promise<number> {
  return (await api.get<Pagina<ReservaResumen>>('/reservas/canales', { params: { canal, pagina: 0 } })).data.totalElementos
}
