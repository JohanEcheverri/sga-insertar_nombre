export interface TemporadaResponse {
  id: string
  nombre: string
  /** Ambas fechas inclusivas; nulas en la temporada base. */
  fechaInicio: string | null
  fechaFin: string | null
  base: boolean
  cantidadTarifas: number
}

export interface CrearTemporadaRequest { nombre: string; fechaInicio: string; fechaFin: string }
export interface AsignarTarifaRequest { temporadaId: string; apartamentoId: string; precioPorNochePorOcupante: number }

export interface DormitorioRequest { nombre: string; cantidadCamas: number; tipoCamas: string }
export interface TarifaInicialRequest { temporadaId: string; precioPorNochePorOcupante: number }
export interface CrearApartamentoRequest {
  identificadorUnico: string
  descripcion: string
  aforoMaximo: number
  dormitorios: DormitorioRequest[]
  tarifas: TarifaInicialRequest[]
}
export interface ModificarApartamentoRequest {
  descripcion: string
  aforoMaximo: number
  dormitorios: DormitorioRequest[]
}

export interface BloqueoResponse {
  id: string
  apartamentoId: string
  fechaInicio: string
  /** Exclusiva, igual que la fecha de salida de una estancia. */
  fechaFin: string
  motivo: string
  activo: boolean
}
export interface CrearBloqueoRequest { apartamentoId: string; fechaInicio: string; fechaFin: string; motivo: string }

export type GravedadNovedad = 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA'
export const GRAVEDADES: GravedadNovedad[] = ['BAJA', 'MEDIA', 'ALTA', 'CRITICA']
export interface NovedadResponse {
  id: string
  apartamentoId: string
  autor: string
  descripcion: string
  gravedad: GravedadNovedad
  fechaReporte: string
}
export interface RegistrarNovedadRequest { autor: string; descripcion: string; gravedad: GravedadNovedad }

export interface IndicadoresConsolidados {
  fechaInicio: string
  fechaFin: string
  ingresosTotales: number
  ocupacionGlobal: number
  reservasTotales: number
  reservasActivas: number
  reservasCanceladas: number
}
export interface ReporteOcupacion {
  fechaInicio: string
  fechaFin: string
  porcentajeOcupacion: number
  nochesVendidas: number
  nochesDisponibles: number
  apartamentosIncluidos: number
}
