interface Props {
  mensaje: string | null | undefined
  tipo?: 'info' | 'exito' | 'error'
  alCerrar?: () => void
}

const CLASES = { info: 'info', exito: 'success', error: 'danger' } as const

export function AlertaNotificacion({ mensaje, tipo = 'info', alCerrar }: Props) {
  if (!mensaje) return null
  return <div role={tipo === 'error' ? 'alert' : 'status'} className={`alert alert-${CLASES[tipo]} d-flex justify-content-between align-items-start gap-2`}>
    <span>{mensaje}</span>
    {alCerrar && <button type="button" className="btn-close" aria-label="Cerrar" onClick={alCerrar} />}
  </div>
}
