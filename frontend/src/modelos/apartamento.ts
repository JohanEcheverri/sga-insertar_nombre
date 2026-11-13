export interface ApartamentoResumen { identificacion: string; nombre?: string; descripcion?: string; capacidad?: number; tarifa?: number; disponible?: boolean; imagenUrl?: string }
export interface TarifaResponse { valor: number; moneda?: string; fechaDesde?: string; fechaHasta?: string }
