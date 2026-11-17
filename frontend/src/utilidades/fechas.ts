function partes(iso: string): [number, number, number] {
  const [anio, mes, dia] = iso.split('-').map(Number)
  return [anio, mes, dia]
}

export function aISO(fecha: Date): string {
  const anio = fecha.getFullYear()
  const mes = String(fecha.getMonth() + 1).padStart(2, '0')
  const dia = String(fecha.getDate()).padStart(2, '0')
  return `${anio}-${mes}-${dia}`
}

export function hoyISO(): string {
  return aISO(new Date())
}

export function sumarDias(iso: string, dias: number): string {
  const [a, m, d] = partes(iso)
  return aISO(new Date(a, m - 1, d + dias))
}

export function primerDiaDelMes(): string {
  const hoy = new Date()
  return aISO(new Date(hoy.getFullYear(), hoy.getMonth(), 1))
}

/** Noches entre entrada y salida: intervalo [entrada, salida) (3.1). */
export function nochesEntre(entrada: string, salida: string): number {
  const [a1, m1, d1] = partes(entrada)
  const [a2, m2, d2] = partes(salida)
  return Math.round((Date.UTC(a2, m2 - 1, d2) - Date.UTC(a1, m1 - 1, d1)) / 86_400_000)
}

export function fechaNacimientoHaceAnios(anios: number): string {
  const hoy = new Date()
  return aISO(new Date(hoy.getFullYear() - anios, hoy.getMonth(), hoy.getDate()))
}

/**
 * Fechas de nacimiento representativas de un grupo para buscar/cotizar sin pedir datos personales:
 * adultos (30 años) y menores (2 años). El umbral real de facturación lo decide el backend.
 */
export function fechasNacimientoGrupo(adultos: number, menores: number): string[] {
  return [
    ...Array.from({ length: adultos }, () => fechaNacimientoHaceAnios(30)),
    ...Array.from({ length: menores }, () => fechaNacimientoHaceAnios(2)),
  ]
}

export function formatearFecha(iso: string | null | undefined): string {
  if (!iso) return '—'
  const [a, m, d] = partes(iso.slice(0, 10))
  return new Date(a, m - 1, d).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })
}

export function formatearFechaHora(iso: string | null | undefined): string {
  if (!iso) return '—'
  return new Date(iso).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' })
}

export function formatearHora(hora: string | null | undefined): string {
  return hora ? hora.slice(0, 5) : '—'
}
