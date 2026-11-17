export interface LineaCotizacion {
  noche: string
  temporada: string
  tarifa: number
  ocupantesFacturables: number
  subtotal: number
}

export interface Cotizacion {
  apartamentoId: string
  ocupantesFacturables: number
  total: number
  lineas: LineaCotizacion[]
}
