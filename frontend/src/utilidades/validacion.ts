import type { ZodType } from 'zod'

export interface ResultadoValidacion<T> {
  datos: T | null
  errores: Record<string, string>
}

/** Valida con Zod y devuelve los mensajes por ruta (p. ej. "dormitorios.0.nombre"). */
export function validar<T>(esquema: ZodType<T>, entrada: unknown): ResultadoValidacion<T> {
  const resultado = esquema.safeParse(entrada)
  if (resultado.success) return { datos: resultado.data, errores: {} }
  const errores: Record<string, string> = {}
  for (const problema of resultado.error.issues) {
    const ruta = problema.path.join('.') || '_'
    if (!errores[ruta]) errores[ruta] = problema.message
  }
  return { datos: null, errores }
}
