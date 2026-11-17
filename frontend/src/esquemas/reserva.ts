import { z } from 'zod'
import { hoyISO } from '../utilidades/fechas'

export const esquemaOcupante = z.object({
  nombre: z.string().trim().min(1, 'El nombre es obligatorio.'),
  apellido: z.string().trim().min(1, 'El apellido es obligatorio.'),
  documento: z.string().trim().min(1, 'El documento es obligatorio.'),
  fechaNacimiento: z.iso.date('Ingresa una fecha de nacimiento válida.'),
}).superRefine((ocupante, ctx) => {
  if (ocupante.fechaNacimiento > hoyISO()) {
    ctx.addIssue({ code: 'custom', path: ['fechaNacimiento'], message: 'La fecha de nacimiento no puede ser futura.' })
  }
})

export const esquemaNuevaReserva = z.object({
  fechaEntrada: z.iso.date('Ingresa la fecha de entrada.'),
  fechaSalida: z.iso.date('Ingresa la fecha de salida.'),
  horaEstimadaLlegada: z.string().regex(/^(?:[01]\d|2[0-3]):[0-5]\d$/, 'Indica una hora válida.'),
  ocupantes: z.array(esquemaOcupante).min(1, 'La reserva necesita al menos un ocupante.'),
}).superRefine((datos, ctx) => {
  if (datos.fechaEntrada && datos.fechaEntrada < hoyISO()) {
    ctx.addIssue({ code: 'custom', path: ['fechaEntrada'], message: 'La entrada no puede ser anterior a hoy.' })
  }
  if (datos.fechaEntrada && datos.fechaSalida && datos.fechaSalida <= datos.fechaEntrada) {
    ctx.addIssue({ code: 'custom', path: ['fechaSalida'], message: 'La salida debe ser posterior a la entrada.' })
  }
})
export type FormularioNuevaReserva = z.infer<typeof esquemaNuevaReserva>
