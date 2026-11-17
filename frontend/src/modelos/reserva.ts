export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'EN_CURSO' | 'FINALIZADA' | 'CANCELADA' | 'NO_SHOW'
export type CanalOrigen = 'PORTAL' | 'DIRECTO' | 'EXTERNO'

export interface OcupanteRequest {
  nombre: string
  apellido: string
  documento: string
  fechaNacimiento: string
}

export type OcupanteResponse = OcupanteRequest

export interface CrearReservaRequest {
  apartamentoId: string
  nombreTitular: string
  apellidoTitular: string
  usuarioId?: string | null
  correoTitular: string
  telefonoTitular: string
  documentoTitular: string
  ocupantes: OcupanteRequest[]
  fechaEntrada: string
  fechaSalida: string
  identificadorExterno?: string
  horaEstimadaLlegada: string
  canalOrigen: CanalOrigen
}

export interface ReservaDetalle {
  codigo: string
  folioId: string
  apartamentoId: string
  nombreTitular: string
  apellidoTitular: string
  usuarioId: string | null
  correoTitular: string
  telefonoTitular: string
  documentoTitular: string
  ocupantes: OcupanteResponse[]
  fechaEntrada: string
  fechaSalida: string
  identificadorExterno: string | null
  horaEstimadaLlegada: string
  canalOrigen: CanalOrigen
  estado: EstadoReserva
  valorCongelado: number
  versionPoliticaCancelacion: string
  fechaCreacion: string
}

export interface CancelacionReservaResponse {
  codigo: string
  estado: EstadoReserva
  porcentajeRetenido: number
  penalidad: number
  saldoAFavor: number
  saldoPendiente: number
  detalle: ReservaDetalle
}

export interface PrevisualizacionCancelacion {
  horasAntelacion: number
  tramo: 1 | 2 | 3
  porcentajeRetenido: number
  penalidad: number
  anticipoRequerido: number
  pagosRegistrados: number
  saldoAFavorEstimado: number
  saldoPendienteEstimado: number
}

export interface ReservaResumen {
  codigo: string
  nombreTitular: string
  apellidoTitular: string
  apartamentoId: string
  fechaEntrada: string
  fechaSalida: string
  estado: EstadoReserva
  valorCongelado: number
}

export interface FiltrosReservas {
  estado?: string
  canal?: string
  documentoTitular?: string
  desde?: string
  hasta?: string
}
