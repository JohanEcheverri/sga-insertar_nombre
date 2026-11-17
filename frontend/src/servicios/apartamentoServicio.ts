import type { ApartamentoResponse, CotizacionResponse, ParametrosDisponibilidad } from '../modelos/apartamento'
import type { Pagina } from '../modelos/comun'
import { api } from './api'

// `indexes: null` serializa los arreglos como fechasNacimiento=a&fechasNacimiento=b (lo que espera Spring).
const SERIALIZADOR = { indexes: null as null }

export async function buscarDisponibles(p: ParametrosDisponibilidad, pagina = 0): Promise<Pagina<ApartamentoResponse>> {
  const { data } = await api.get<Pagina<ApartamentoResponse>>('/apartamentos/disponibilidad', {
    params: { ...p, pagina }, paramsSerializer: SERIALIZADOR,
  })
  return data
}

export async function buscarPorCapacidad(capacidad: number, pagina = 0): Promise<Pagina<ApartamentoResponse>> {
  const { data } = await api.get<Pagina<ApartamentoResponse>>('/apartamentos/buscar-capacidad', { params: { capacidad, pagina } })
  return data
}

/** El backend no expone GET /apartamentos: se recorre buscar-capacidad (capacidad 1 = todos los activos). */
export async function listarTodosLosApartamentos(): Promise<ApartamentoResponse[]> {
  const todos: ApartamentoResponse[] = []
  let pagina = 0
  let total = 1
  do {
    const respuesta = await buscarPorCapacidad(1, pagina)
    todos.push(...respuesta.contenido)
    total = respuesta.totalPaginas
    pagina += 1
  } while (pagina < total)
  return todos
}

export async function obtenerApartamento(id: string): Promise<ApartamentoResponse> {
  const encontrado = (await listarTodosLosApartamentos()).find((a) => a.id === id)
  if (!encontrado) throw new Error('No encontramos el apartamento solicitado.')
  return encontrado
}

export async function apartamentoDisponible(id: string, p: ParametrosDisponibilidad): Promise<boolean> {
  let pagina = 0
  let total = 1
  do {
    const respuesta = await buscarDisponibles(p, pagina)
    if (respuesta.contenido.some((a) => a.id === id)) return true
    total = respuesta.totalPaginas
    pagina += 1
  } while (pagina < total)
  return false
}

export async function cotizar(apartamentoId: string, p: ParametrosDisponibilidad): Promise<CotizacionResponse> {
  const { data } = await api.get<CotizacionResponse>('/cotizaciones', {
    params: { apartamentoId, fechaEntrada: p.fechaEntrada, fechaSalida: p.fechaSalida, fechasNacimiento: p.fechasNacimiento },
    paramsSerializer: SERIALIZADOR,
  })
  return data
}
