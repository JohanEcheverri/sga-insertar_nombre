import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { InsigniaEstado } from '../componentes/InsigniaEstado'
import { Paginacion } from '../componentes/Paginacion'
import { useAuth } from '../contexto/useAuth'
import { useAviso } from '../hooks/useAviso'
import type { EstadoReserva, ReservaResumen } from '../modelos/reserva'
import { listarTodosLosApartamentos } from '../servicios/apartamentoServicio'
import { cancelarReserva, listarMisReservas } from '../servicios/reservaServicio'
import { formatearFecha, nochesEntre } from '../utilidades/fechas'
import { codigoCorto, formatearMoneda } from '../utilidades/formato'
import { obtenerUsuarioId } from '../utilidades/jwt'

const TAMANIO = 10
const ESTADOS: EstadoReserva[] = ['PENDIENTE', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA', 'NO_SHOW']

export default function MisReservas() {
  const { sesion } = useAuth()
  const usuarioId = obtenerUsuarioId(sesion?.token)
  const estado = useLocation().state
  const [mensajeInicial, setMensajeInicial] = useState(() => typeof estado === 'object' && estado !== null && 'mensaje' in estado && typeof estado.mensaje === 'string' ? estado.mensaje : '')
  const { aviso, exito, fallo, limpiar } = useAviso()
  const [reservas, setReservas] = useState<ReservaResumen[]>([])
  const [apartamentos, setApartamentos] = useState<Record<string, string>>({})
  const [cargando, setCargando] = useState(true)
  const [filtro, setFiltro] = useState('')
  const [pagina, setPagina] = useState(1)
  const [confirmando, setConfirmando] = useState<string | null>(null)
  const [cancelando, setCancelando] = useState<string | null>(null)

  const cargar = useCallback(async () => {
    if (!usuarioId) { setCargando(false); return }
    setCargando(true)
    try {
      const [resultadoLista, resultadoApartamentos] = await Promise.allSettled([listarMisReservas(usuarioId), listarTodosLosApartamentos()])
      if (resultadoLista.status === 'rejected') throw resultadoLista.reason
      setReservas(resultadoLista.value)
      if (resultadoApartamentos.status === 'fulfilled') {
        setApartamentos(Object.fromEntries(resultadoApartamentos.value.map((a) => [a.id, a.identificadorUnico])))
      } else {
        setApartamentos({})
        fallo(resultadoApartamentos.reason)
      }
    } catch (causa) {
      fallo(causa)
    } finally {
      setCargando(false)
    }
  }, [usuarioId, fallo])

  useEffect(() => { void cargar() }, [cargar])

  const cancelar = async (codigo: string) => {
    setCancelando(codigo)
    try {
      const detalle = await cancelarReserva(codigo)
      exito(`Reserva cancelada. La retención, si aplica, se calcula con la política ${detalle.versionPoliticaCancelacion} vigente cuando reservaste.`)
      setConfirmando(null)
      await cargar()
    } catch (causa) {
      fallo(causa)
    } finally {
      setCancelando(null)
    }
  }

  const filtradas = reservas.filter((r) => !filtro || r.estado === filtro)
  const totalPaginas = Math.max(1, Math.ceil(filtradas.length / TAMANIO))
  const visibles = filtradas.slice((pagina - 1) * TAMANIO, pagina * TAMANIO)

  return <main className="container py-4">
    <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
      <h1 className="h3 mb-0">Mis reservas</h1>
      <div className="d-flex gap-2">
        <select className="form-select" aria-label="Filtrar por estado" value={filtro} onChange={(e) => { setFiltro(e.target.value); setPagina(1) }}>
          <option value="">Todos los estados</option>
          {ESTADOS.map((e) => <option key={e} value={e}>{e.replace('_', ' ')}</option>)}
        </select>
        <Link className="btn btn-primary" to="/">Nueva reserva</Link>
      </div>
    </div>

    <AlertaNotificacion mensaje={aviso?.mensaje ?? mensajeInicial} tipo={aviso?.tipo ?? 'exito'} alCerrar={() => { limpiar(); setMensajeInicial('') }} />
    {cargando ? <p className="text-secondary">Cargando tus reservas…</p>
      : visibles.length === 0 ? <AlertaNotificacion mensaje="Aún no tienes reservas con ese criterio." />
        : <div className="row g-3">{visibles.map((r) => {
          const cancelable = r.estado === 'PENDIENTE' || r.estado === 'CONFIRMADA'
          return <div className="col-12" key={r.codigo}><article className="card shadow-sm"><div className="card-body">
            <div className="d-flex flex-wrap justify-content-between align-items-start gap-2">
              <div>
                <h2 className="h6 mb-1">Apartamento {apartamentos[r.apartamentoId] ?? '—'} · <code>{codigoCorto(r.codigo)}</code></h2>
                <p className="mb-1">{formatearFecha(r.fechaEntrada)} → {formatearFecha(r.fechaSalida)} <span className="text-secondary">({nochesEntre(r.fechaEntrada, r.fechaSalida)} noches)</span></p>
                <p className="mb-0 fw-semibold">{formatearMoneda(r.valorCongelado)}</p>
              </div>
              <div className="text-end">
                <InsigniaEstado estado={r.estado} />
                {cancelable && <div className="mt-2">
                  {confirmando === r.codigo
                    ? <div className="d-flex gap-2 align-items-center"><span className="small">¿Cancelar?</span>
                      <button className="btn btn-danger btn-sm" type="button" disabled={cancelando === r.codigo} onClick={() => void cancelar(r.codigo)}>{cancelando === r.codigo ? 'Cancelando…' : 'Sí, cancelar'}</button>
                      <button className="btn btn-outline-secondary btn-sm" type="button" onClick={() => setConfirmando(null)}>No</button></div>
                    : <button className="btn btn-outline-danger btn-sm" type="button" onClick={() => setConfirmando(r.codigo)}>Cancelar reserva</button>}
                </div>}
              </div>
            </div>
          </div></article></div>
        })}</div>}
    <div className="mt-4"><Paginacion pagina={pagina} totalPaginas={totalPaginas} alCambiar={setPagina} /></div>
  </main>
}
