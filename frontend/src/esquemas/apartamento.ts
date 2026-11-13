import { z } from 'zod'
export const esquemaApartamento = z.object({ identificacion: z.string().min(1), nombre: z.string().min(1), capacidad: z.number().int().positive(), tarifa: z.number().nonnegative() })
export type FormularioApartamento = z.infer<typeof esquemaApartamento>
