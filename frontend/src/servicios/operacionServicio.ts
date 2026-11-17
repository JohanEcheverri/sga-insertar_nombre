import type { ApartamentoResponse } from '../modelos/apartamento'
import type { Pagina } from '../modelos/comun'
import type { NovedadResponse, RegistrarNovedadRequest } from '../modelos/admin'
import { api } from './api'

export async function listarNovedades(apartamentoId: string): Promise<NovedadResponse[]> {
  return (await api.get<NovedadResponse[]>(`/apartamentos/${apartamentoId}/novedades`)).data
}

export async function crearNovedad(apartamentoId: string, datos: RegistrarNovedadRequest): Promise<NovedadResponse> {
  return (await api.post<NovedadResponse>(`/apartamentos/${apartamentoId}/novedades`, datos)).data
}

export async function eliminarNovedad(id: string): Promise<void> {
  await api.delete(`/novedades/${id}`)
}

export async function listarPendientesPreparacion(pagina = 0): Promise<Pagina<ApartamentoResponse>> {
  return (await api.get<Pagina<ApartamentoResponse>>('/apartamentos/preparacion-pendiente', { params: { pagina } })).data
}
