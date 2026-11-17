import { etiqueta } from '../utilidades/formato'

const COLORES: Record<string, string> = {
  PENDIENTE: 'warning', CONFIRMADA: 'primary', EN_CURSO: 'success', FINALIZADA: 'secondary', CANCELADA: 'danger', NO_SHOW: 'dark',
  ABIERTO: 'success', CERRADO: 'secondary',
  PREPARADO: 'success', OCUPADO: 'primary', PENDIENTE_PREPARACION: 'warning', EN_PREPARACION: 'info', FUERA_DE_SERVICIO: 'danger',
}

export function InsigniaEstado({ estado }: { estado: string }) {
  return <span className={`badge text-bg-${COLORES[estado] ?? 'secondary'}`}>{etiqueta(estado)}</span>
}
