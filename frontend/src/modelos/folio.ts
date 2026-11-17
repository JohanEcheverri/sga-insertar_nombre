export type EstadoFolio = 'ABIERTO' | 'CERRADO'
export type MedioPago = 'EFECTIVO' | 'TARJETA_CREDITO' | 'TARJETA_DEBITO' | 'TRANSFERENCIA' | 'PLATAFORMA_EXTERNA'
export type TipoCargoManual = 'SERVICIO_ADICIONAL' | 'AJUSTE_MODIFICACION' | 'AJUSTE_A_FAVOR'

export const MEDIOS_PAGO: MedioPago[] = ['EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'PLATAFORMA_EXTERNA']
export const TIPOS_CARGO_MANUAL: TipoCargoManual[] = ['SERVICIO_ADICIONAL', 'AJUSTE_MODIFICACION', 'AJUSTE_A_FAVOR']
/** Cargos de tipo crédito restan del total (movimiento inverso, RN-16). */
export const TIPOS_CREDITO = ['AJUSTE_A_FAVOR', 'REVERSO_ALOJAMIENTO']

export interface CargoResponse {
  tipoCargo: string
  concepto: string
  monto: number
  fechaRegistro: string
  esCredito: boolean
}

export interface AutorizacionCierreResponse {
  autor: string
  motivo: string
  fecha: string
}

export interface FolioResponse {
  id: string
  reservaId: string
  estado: EstadoFolio
  cargos: CargoResponse[]
  pagos: { medio: MedioPago; monto: number; referencia: string | null; fechaRegistro: string }[]
  cantidadPagos: number
  totalCargos: number
  totalPagos: number
  /** Saldo con signo: positivo debe el huésped, negativo saldo a favor. */
  saldoPendiente: number
  saldoAFavor: number
  autorizacionCierre: AutorizacionCierreResponse | null
}

export interface PagoRequest {
  monto: number
  medio: MedioPago
  referencia?: string
}

export interface CargoRequest {
  tipoCargo: TipoCargoManual
  concepto: string
  monto: number
}
