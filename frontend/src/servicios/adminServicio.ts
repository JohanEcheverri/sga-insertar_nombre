import type { ApartamentoResponse, EstadoOperativo } from '../modelos/apartamento'
import type {
  AsignarTarifaRequest, BloqueoResponse, CrearApartamentoRequest, CrearBloqueoRequest, CrearTemporadaRequest,
  IndicadoresConsolidados, ModificarApartamentoRequest, ReporteOcupacion, TemporadaResponse,
} from '../modelos/admin'
import type { Pagina } from '../modelos/comun'
import { api } from './api'

export async function listarTemporadas(): Promise<TemporadaResponse[]> {
  const todas: TemporadaResponse[] = []
  let pagina = 0
  let total = 1
  do {
    const { data } = await api.get<Pagina<TemporadaResponse>>('/temporadas', { params: { pagina } })
    todas.push(...data.contenido)
    total = data.totalPaginas
    pagina += 1
  } while (pagina < total)
  return todas
}

export async function crearTemporada(datos: CrearTemporadaRequest): Promise<TemporadaResponse> {
  return (await api.post<TemporadaResponse>('/temporadas', datos)).data
}

export async function modificarTemporada(id: string, datos: CrearTemporadaRequest): Promise<TemporadaResponse> {
  return (await api.put<TemporadaResponse>(`/temporadas/${id}`, datos)).data
}

export async function retirarTemporada(id: string): Promise<void> {
  await api.put(`/temporadas/${id}/retirar`)
}

export async function asignarTarifa(datos: AsignarTarifaRequest): Promise<TemporadaResponse> {
  return (await api.post<TemporadaResponse>('/tarifas', datos)).data
}

export async function crearApartamento(datos: CrearApartamentoRequest): Promise<ApartamentoResponse> {
  return (await api.post<ApartamentoResponse>('/apartamentos', datos)).data
}

export async function modificarApartamento(id: string, datos: ModificarApartamentoRequest): Promise<ApartamentoResponse> {
  return (await api.put<ApartamentoResponse>(`/apartamentos/${id}`, datos)).data
}

export async function retirarApartamento(id: string): Promise<void> {
  await api.delete(`/apartamentos/${id}`)
}

export async function cambiarEstadoOperativo(id: string, estadoOperativo: EstadoOperativo): Promise<ApartamentoResponse> {
  return (await api.patch<ApartamentoResponse>(`/apartamentos/${id}/estado-operativo`, { estadoOperativo })).data
}

export async function listarBloqueos(apartamentoId: string): Promise<BloqueoResponse[]> {
  return (await api.get<BloqueoResponse[]>(`/apartamentos/${apartamentoId}/bloqueos`)).data
}

export async function crearBloqueo(datos: CrearBloqueoRequest): Promise<BloqueoResponse> {
  return (await api.post<BloqueoResponse>(`/apartamentos/${datos.apartamentoId}/bloqueos`, datos)).data
}

export async function reporteConsolidado(fechaInicio: string, fechaFin: string): Promise<IndicadoresConsolidados> {
  return (await api.get<IndicadoresConsolidados>('/reportes', { params: { fechaInicio, fechaFin } })).data
}

export async function reporteOcupacion(fechaInicio: string, fechaFin: string): Promise<ReporteOcupacion> {
  return (await api.get<ReporteOcupacion>('/reportes/ocupacion', { params: { fechaInicio, fechaFin } })).data
}
