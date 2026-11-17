import type { AccionReserva } from '../hooks/useAccionesReserva'
import type { ReservaResumen } from '../modelos/reserva'
import { formatearFecha } from '../utilidades/fechas'
import { codigoCorto, formatearMoneda } from '../utilidades/formato'
import { InsigniaEstado } from './InsigniaEstado'

interface Props {
  reservas: ReservaResumen[]
  apartamentos: Record<string, string>
  ocupado: string | null
  alAccionar: (accion: AccionReserva, reserva: ReservaResumen) => void
}

const ACCIONES: Record<string, [AccionReserva, string, string][]> = {
  PENDIENTE: [['confirmar', 'Confirmar', 'success'], ['cancelar', 'Cancelar', 'outline-danger']],
  CONFIRMADA: [['llegada', 'Registrar llegada', 'primary'], ['no-show', 'No-show', 'outline-dark'], ['cancelar', 'Cancelar', 'outline-danger']],
  EN_CURSO: [['salida', 'Registrar salida', 'primary']],
}

export function TablaReservas({ reservas, apartamentos, ocupado, alAccionar }: Props) {
  if (reservas.length === 0) return <p className="text-secondary mb-0">No hay reservas para mostrar.</p>
  return <div className="table-responsive">
    <table className="table table-sm align-middle">
      <thead><tr><th>Código</th><th>Titular</th><th>Apto.</th><th>Entrada</th><th>Salida</th><th>Estado</th><th className="text-end">Valor</th><th>Acciones</th></tr></thead>
      <tbody>
        {reservas.map((r) => <tr key={r.codigo}>
          <td><code>{codigoCorto(r.codigo)}</code></td>
          <td>{r.nombreTitular} {r.apellidoTitular}</td>
          <td>{apartamentos[r.apartamentoId] ?? '—'}</td>
          <td>{formatearFecha(r.fechaEntrada)}</td>
          <td>{formatearFecha(r.fechaSalida)}</td>
          <td><InsigniaEstado estado={r.estado} /></td>
          <td className="text-end">{formatearMoneda(r.valorCongelado)}</td>
          <td><div className="d-flex flex-wrap gap-1">
            {(ACCIONES[r.estado] ?? []).map(([accion, texto, color]) =>
              <button key={accion} type="button" className={`btn btn-sm btn-${color}`} disabled={ocupado === r.codigo} onClick={() => alAccionar(accion, r)}>{texto}</button>)}
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={() => alAccionar('folio', r)}>Folio</button>
          </div></td>
        </tr>)}
      </tbody>
    </table>
  </div>
}
