import type { CargoRequest, FolioResponse, PagoRequest } from '../modelos/folio'
import { api } from './api'

export async function obtenerFolio(codigoReserva: string): Promise<FolioResponse> {
  return (await api.get<FolioResponse>(`/reservas/${codigoReserva}/folio`)).data
}

export async function registrarPago(codigoReserva: string, pago: PagoRequest): Promise<FolioResponse> {
  return (await api.post<FolioResponse>(`/reservas/${codigoReserva}/folio/pagos`, pago)).data
}

/** El backend responde solo el cargo creado; se recarga el folio completo. */
export async function registrarCargo(codigoReserva: string, cargo: CargoRequest): Promise<FolioResponse> {
  await api.post(`/reservas/${codigoReserva}/folio/cargos`, cargo)
  return obtenerFolio(codigoReserva)
}

export async function cerrarFolio(codigoReserva: string, motivo: string): Promise<FolioResponse> {
  return (await api.put<FolioResponse>(`/reservas/${codigoReserva}/folio/cerrar`, { motivo })).data
}
