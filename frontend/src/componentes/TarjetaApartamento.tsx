import { Link } from 'react-router'
import type { ApartamentoResponse } from '../modelos/apartamento'
import { formatearMoneda } from '../utilidades/formato'

interface Props {
  apartamento: ApartamentoResponse
  /** Valor estimado de la estancia buscada (si hay fechas). */
  total?: number
  /** Query string con fechas y grupo para propagar al detalle y a la reserva. */
  consulta?: string
}

export function TarjetaApartamento({ apartamento, total, consulta = '' }: Props) {
  const qs = consulta ? `?${consulta}` : ''
  return <article className="card h-100 shadow-sm">
    <div className="bg-secondary-subtle d-flex align-items-center justify-content-center" style={{ height: 130 }}>
      <span className="display-6 fw-bold text-secondary">{apartamento.identificadorUnico}</span>
    </div>
    <div className="card-body d-flex flex-column">
      <h2 className="h5">Apartamento {apartamento.identificadorUnico}</h2>
      <p className="text-secondary small">{apartamento.descripcion}</p>
      <ul className="list-unstyled small mb-3">
        <li>Capacidad: <strong>{apartamento.aforoMaximo}</strong> personas</li>
        <li>Dormitorios: <strong>{apartamento.dormitorios.length}</strong></li>
      </ul>
      {total !== undefined && <p className="fw-semibold mb-3">{formatearMoneda(total)} <span className="text-secondary small fw-normal">total estimado</span></p>}
      <div className="mt-auto d-flex gap-2">
        <Link className="btn btn-outline-primary btn-sm" to={`/apartamentos/${apartamento.id}${qs}`}>Ver detalle</Link>
        <Link className="btn btn-primary btn-sm" to={`/reservar/${apartamento.id}${qs}`}>Reservar</Link>
      </div>
    </div>
  </article>
}
