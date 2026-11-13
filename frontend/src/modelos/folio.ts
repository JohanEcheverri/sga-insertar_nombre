export type EstadoFolio = 'ABIERTO' | 'CERRADO' | 'PAGADO'
export interface CargoRequest { descripcion: string; cantidad: number; valorUnitario: number }
export interface CargoResponse extends CargoRequest { id: number; subtotal: number; fecha?: string }
export interface FolioResumen { id: number; reservaId: number; estado: EstadoFolio; total: number }
export interface FolioDetalle extends FolioResumen { cargos: CargoResponse[] }
