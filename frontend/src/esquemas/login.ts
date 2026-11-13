import { z } from 'zod'
export const esquemaLogin = z.object({ correo: z.email(), clave: z.string().min(1, 'La clave es obligatoria.') })
export type FormularioLogin = z.infer<typeof esquemaLogin>
