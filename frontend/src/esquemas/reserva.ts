import { z } from 'zod'
export const esquemaNuevaReserva = z.object({
  identificacionApartamento: z.string().min(1, 'Selecciona un apartamento.'),
  fechaEntrada: z.iso.date(),
  fechaSalida: z.iso.date(),
  ocupantes: z.array(z.object({ nombres: z.string().min(1), apellidos: z.string().min(1), tipoDocumento: z.string().min(1), documento: z.string().min(1) })).min(1),
}).refine((datos) => datos.fechaSalida > datos.fechaEntrada, { message: 'La salida debe ser posterior a la entrada.', path: ['fechaSalida'] })
export type FormularioNuevaReserva = z.infer<typeof esquemaNuevaReserva>
