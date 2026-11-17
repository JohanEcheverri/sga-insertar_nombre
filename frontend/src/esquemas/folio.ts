import { z } from 'zod'

const monto = z.coerce.number({ error: 'Monto inválido.' }).positive('El monto debe ser mayor que cero.')

export const esquemaPago = z.object({
  monto,
  medio: z.enum(['EFECTIVO', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'PLATAFORMA_EXTERNA'], { error: 'Selecciona un medio de pago.' }),
  referencia: z.string().trim().optional(),
})
export type FormularioPago = z.infer<typeof esquemaPago>

export const esquemaCargo = z.object({
  tipoCargo: z.enum(['SERVICIO_ADICIONAL', 'AJUSTE_MODIFICACION', 'AJUSTE_A_FAVOR'], { error: 'Selecciona el tipo de cargo.' }),
  concepto: z.string().trim().min(1, 'El concepto es obligatorio.'),
  monto,
})
export type FormularioCargo = z.infer<typeof esquemaCargo>

export const esquemaCierreFolio = z.object({
  motivo: z.string().trim().min(1, 'Indica el motivo del cierre.'),
})
