import { z } from 'zod'
export const esquemaLogin = z.object({
  username: z.email('Ingresa un correo válido.'),
  password: z.string().min(1, 'La contraseña es obligatoria.'),
})
export type FormularioLogin = z.infer<typeof esquemaLogin>
