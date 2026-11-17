import { z } from 'zod'

export const esquemaTemporada = z.object({
  nombre: z.string().trim().min(1, 'El nombre es obligatorio.'),
  fechaInicio: z.iso.date('Ingresa la fecha de inicio.'),
  fechaFin: z.iso.date('Ingresa la fecha de fin.'),
}).refine((d) => d.fechaFin >= d.fechaInicio, { message: 'La fecha final no puede ser anterior a la inicial.', path: ['fechaFin'] })

export const esquemaTarifa = z.object({
  temporadaId: z.string().min(1, 'Selecciona la temporada.'),
  apartamentoId: z.string().min(1, 'Selecciona el apartamento.'),
  precio: z.coerce.number({ error: 'Precio inválido.' }).positive('El precio debe ser mayor que cero.'),
})

export const esquemaBloqueo = z.object({
  fechaInicio: z.iso.date('Ingresa la fecha de inicio.'),
  fechaFin: z.iso.date('Ingresa la fecha de fin.'),
  motivo: z.string().trim().min(1, 'El motivo es obligatorio.'),
}).refine((d) => d.fechaFin > d.fechaInicio, { message: 'La fecha final (exclusiva) debe ser posterior a la inicial.', path: ['fechaFin'] })

export const esquemaNovedad = z.object({
  autor: z.string().trim().min(1, 'El autor es obligatorio.'),
  descripcion: z.string().trim().min(1, 'La descripción es obligatoria.'),
  gravedad: z.enum(['BAJA', 'MEDIA', 'ALTA', 'CRITICA'], { error: 'Selecciona la gravedad.' }),
})

export const esquemaRangoReporte = z.object({
  fechaInicio: z.iso.date('Ingresa la fecha de inicio.'),
  fechaFin: z.iso.date('Ingresa la fecha de fin.'),
}).refine((d) => d.fechaFin > d.fechaInicio, { message: 'La fecha final (exclusiva) debe ser posterior a la inicial.', path: ['fechaFin'] })
