export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'FINALIZADA' | 'EN_CURSO'
export type CanalOrigen = 'WEB' | 'TELEFONO' | 'PRESENCIAL' | 'OTRO'
export interface OcupanteRequest { nombres: string; apellidos: string; tipoDocumento: string; documento: string }
export interface CrearReservaRequest { identificacionApartamento: string; fechaEntrada: string; fechaSalida: string; ocupantes: OcupanteRequest[]; canalOrigen?: CanalOrigen }
export interface OcupanteResponse extends OcupanteRequest { id?: number }
export interface ReservaDetalle { id: number; fechaEntrada: string; fechaSalida: string; estado: EstadoReserva; identificacionApartamento: string; ocupantes: OcupanteResponse[]; total?: number }
