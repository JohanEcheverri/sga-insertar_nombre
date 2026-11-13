import type { ApartamentoResumen } from '../modelos/apartamento'
import { api } from './api'
export async function listarApartamentos(): Promise<ApartamentoResumen[]> { return (await api.get<ApartamentoResumen[]>('/apartamentos')).data }
export async function obtenerApartamento(identificacion: string): Promise<ApartamentoResumen> { return (await api.get<ApartamentoResumen>(`/apartamentos/${identificacion}`)).data }
