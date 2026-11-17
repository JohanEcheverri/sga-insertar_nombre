import type { Cotizacion } from '../modelos/cotizacion'
import { api } from './api'

export async function cotizar(apartamentoId: string, entrada: string, salida: string, fechasNacimiento: string[]): Promise<Cotizacion> {
  const { data } = await api.get<Cotizacion>('/cotizaciones', {
    params: { apartamentoId, fechaEntrada: entrada, fechaSalida: salida, fechasNacimiento },
    paramsSerializer: { indexes: null },
  })
  return data
}
