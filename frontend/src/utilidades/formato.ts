const FORMATO_COP = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
export function formatearMoneda(valor: number): string { return FORMATO_COP.format(valor) }
