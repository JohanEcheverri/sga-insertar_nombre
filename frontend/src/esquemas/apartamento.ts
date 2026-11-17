import { z } from 'zod'
import { hoyISO } from '../utilidades/fechas'

/** Filtros del catálogo (Inicio). Las fechas son opcionales, pero si hay una deben estar ambas. */
export const esquemaBusqueda = z.object({
  fechaEntrada: z.string(),
  fechaSalida: z.string(),
  adultos: z.number({ error: 'Indica cuántos adultos.' }).int('Debe ser un número entero.').min(1, 'Mínimo 1 adulto.').max(20, 'Máximo 20.'),
  menores: z.number({ error: 'Indica los menores (0 si no hay).' }).int('Debe ser un número entero.').min(0, 'No puede ser negativo.').max(20, 'Máximo 20.'),
  precioMaximo: z.string(),
}).superRefine((datos, ctx) => {
  if (datos.precioMaximo.trim() !== '' && !(Number(datos.precioMaximo) >= 0)) {
    ctx.addIssue({ code: 'custom', path: ['precioMaximo'], message: 'Indica un valor válido.' })
  }
  if (datos.precioMaximo.trim() !== '' && (!datos.fechaEntrada || !datos.fechaSalida)) {
    ctx.addIssue({ code: 'custom', path: ['precioMaximo'], message: 'Indica fechas para poder filtrar por precio.' })
  }
  if (!datos.fechaEntrada && !datos.fechaSalida) return
  if (!datos.fechaEntrada) ctx.addIssue({ code: 'custom', path: ['fechaEntrada'], message: 'Indica la fecha de entrada.' })
  if (!datos.fechaSalida) ctx.addIssue({ code: 'custom', path: ['fechaSalida'], message: 'Indica la fecha de salida.' })
  if (datos.fechaEntrada && datos.fechaSalida) {
    if (datos.fechaEntrada < hoyISO()) ctx.addIssue({ code: 'custom', path: ['fechaEntrada'], message: 'La entrada no puede ser anterior a hoy.' })
    if (datos.fechaSalida <= datos.fechaEntrada) ctx.addIssue({ code: 'custom', path: ['fechaSalida'], message: 'La salida debe ser posterior a la entrada.' })
  }
})
export type FormularioBusqueda = z.infer<typeof esquemaBusqueda>

/** Formulario de administración de apartamentos (los números llegan como texto desde los inputs). */
export const esquemaDormitorio = z.object({
  nombre: z.string().trim().min(1, 'El nombre es obligatorio.'),
  cantidadCamas: z.coerce.number({ error: 'Número de camas inválido.' }).int('Debe ser entero.').min(1, 'Mínimo 1 cama.'),
  tipoCamas: z.string().trim().min(1, 'El tipo de camas es obligatorio.'),
})

export const esquemaApartamento = z.object({
  identificadorUnico: z.string().trim().min(1, 'El identificador es obligatorio.'),
  descripcion: z.string().trim().min(1, 'La descripción es obligatoria.'),
  aforoMaximo: z.coerce.number({ error: 'Capacidad inválida.' }).int('Debe ser entera.').min(1, 'Mínimo 1 persona.').max(127, 'Máximo 127.'),
  dormitorios: z.array(esquemaDormitorio).min(1, 'Registra al menos un dormitorio.'),
})
export type FormularioApartamento = z.infer<typeof esquemaApartamento>
