export type EstadoOperativo = 'PREPARADO' | 'OCUPADO' | 'PENDIENTE_PREPARACION' | 'EN_PREPARACION' | 'FUERA_DE_SERVICIO'

export interface ApartamentoResponse {
  id: string
  identificadorUnico: string
  descripcion: string
  aforoMaximo: number
  estadoOperativo: EstadoOperativo
  dormitorios: string[]
  disponible: boolean
}

export interface ParametrosDisponibilidad {
  fechaEntrada: string
  fechaSalida: string
  fechasNacimiento: string[]
}

export interface LineaCotizacion {
  noche: string
  temporada: string
  tarifa: number
  ocupantesFacturables: number
  subtotal: number
}

export interface CotizacionResponse {
  apartamentoId: string
  ocupantesFacturables: number
  total: number
  lineas: LineaCotizacion[]
}
