import type { CrearReservaRequest, ReservaDetalle } from '../modelos/reserva'
import { api } from './api'
export async function crearReserva(datos: CrearReservaRequest): Promise<ReservaDetalle> { return (await api.post<ReservaDetalle>('/reservas', datos)).data }
export async function listarMisReservas(): Promise<ReservaDetalle[]> { return (await api.get<ReservaDetalle[]>('/reservas/mias')).data }
