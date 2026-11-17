const FORMATO_COP = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })

export function formatearMoneda(valor: number | string | null | undefined): string {
  const numero = Number(valor ?? 0)
  return FORMATO_COP.format(Number.isFinite(numero) ? numero : 0)
}

export function etiqueta(texto: string): string {
  const limpio = texto.replaceAll('_', ' ').toLowerCase()
  return limpio.charAt(0).toUpperCase() + limpio.slice(1)
}

export function codigoCorto(codigo: string): string {
  return codigo.slice(0, 8).toUpperCase()
}
