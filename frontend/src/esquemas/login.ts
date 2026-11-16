import { z } from 'zod'
import { hoyISO } from '../utilidades/fechas'

export const loginSchema = z.object({
  username: z.email('Ingresa un correo válido.'),
  password: z.string().min(1, 'La contraseña es obligatoria.'),
})
export type FormularioLogin = z.infer<typeof loginSchema>

export const registroSchema = z.object({
  nombre: z.string().trim().min(1, 'El nombre es obligatorio.'),
  apellido: z.string().trim().min(1, 'El apellido es obligatorio.'),
  documento: z.string().trim().min(1, 'El documento es obligatorio.'),
  correo: z.email('Ingresa un correo válido.'),
  telefono: z.string().trim().min(1, 'El teléfono es obligatorio.'),
  password: z.string().min(8, 'La contraseña debe tener al menos 8 caracteres.'),
  confirmarPassword: z.string().min(1, 'Confirma tu contraseña.'),
  fechaNacimiento: z.iso.date('Ingresa una fecha válida.'),
}).refine((datos) => datos.password === datos.confirmarPassword, {
  message: 'Las contraseñas no coinciden.',
  path: ['confirmarPassword'],
}).refine((datos) => datos.fechaNacimiento <= hoyISO(), {
  message: 'La fecha de nacimiento no puede ser futura.',
  path: ['fechaNacimiento'],
})
export type FormularioRegistro = z.infer<typeof registroSchema>
