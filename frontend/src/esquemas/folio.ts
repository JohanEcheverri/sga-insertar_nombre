import { z } from 'zod'
export const esquemaCargo = z.object({ descripcion: z.string().min(1), cantidad: z.number().positive(), valorUnitario: z.number().nonnegative() })
export type FormularioCargo = z.infer<typeof esquemaCargo>
