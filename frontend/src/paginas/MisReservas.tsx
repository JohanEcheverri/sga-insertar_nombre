import { useCallback, useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { InsigniaEstado } from '../componentes/InsigniaEstado'
import { Paginacion } from '../componentes/Paginacion'
import { useAuth } from '../contexto/useAuth'
import { useAviso } from '../hooks/useAviso'
import type { EstadoReserva, ReservaResumen } from '../modelos/reserva'
import { listarTodosLosApartamentos } from '../servicios/apartamentoServicio'
import { cancelarReserva, listarMisReservas, previsualizarCancelacion } from '../servicios/reservaServicio'
import { formatearFecha, nochesEntre } from '../utilidades/fechas'
import { codigoCorto, formatearMoneda } from '../utilidades/formato'
import { obtenerUsuarioId } from '../utilidades/jwt'
import { obtenerFolio } from '../servicios/folioServicio'
import type { FolioResponse } from '../modelos/folio'
import type { PrevisualizacionCancelacion } from '../modelos/reserva'

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
  const [preparandoCancelacion, setPreparandoCancelacion] = useState<string | null>(null)
  const [previsualizaciones, setPrevisualizaciones] = useState<Record<string, PrevisualizacionCancelacion>>({})
  const [folioVisible, setFolioVisible] = useState<string | null>(null)

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
      const anterior = previsualizaciones[codigo]
      const actual = await previsualizarCancelacion(codigo)
      if (anterior && (anterior.tramo !== actual.tramo || anterior.penalidad !== actual.penalidad
        || anterior.pagosRegistrados !== actual.pagosRegistrados || anterior.saldoAFavorEstimado !== actual.saldoAFavorEstimado
        || anterior.saldoPendienteEstimado !== actual.saldoPendienteEstimado)) {
        setPrevisualizaciones((actuales) => ({ ...actuales, [codigo]: actual }))
        exito('La liquidación cambió desde que abriste la confirmación. Revisa los importes actualizados antes de confirmar.')
        return
      }
      const resultado = await cancelarReserva(codigo)
      exito(`Reserva cancelada. Penalidad liquidada: ${formatearMoneda(resultado.penalidad)}. Saldo a favor: ${formatearMoneda(resultado.saldoAFavor)}; saldo pendiente: ${formatearMoneda(resultado.saldoPendiente)}.`)
      setConfirmando(null)
      await cargar()
    } catch (causa) {
      fallo(causa)
    } finally {
      setCancelando(null)
    }
  }

  const prepararCancelacion = async (r: ReservaResumen) => {
    setPreparandoCancelacion(r.codigo)
    try {
      const previsualizacion = await previsualizarCancelacion(r.codigo)
      setPrevisualizaciones((actuales) => ({ ...actuales, [r.codigo]: previsualizacion }))
      setConfirmando(r.codigo)
    } catch (causa) { fallo(causa) } finally { setPreparandoCancelacion(null) }
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
          const previsualizacion = previsualizaciones[r.codigo]
          return <div className="col-12" key={r.codigo}><article className="card shadow-sm"><div className="card-body">
            <div className="d-flex flex-wrap justify-content-between align-items-start gap-2">
              <div>
                <h2 className="h6 mb-1">Apartamento {apartamentos[r.apartamentoId] ?? '—'} · <code>{codigoCorto(r.codigo)}</code></h2>
                <p className="mb-1">{formatearFecha(r.fechaEntrada)} → {formatearFecha(r.fechaSalida)} <span className="text-secondary">({nochesEntre(r.fechaEntrada, r.fechaSalida)} noches)</span></p>
                <p className="mb-0 fw-semibold">{formatearMoneda(r.valorCongelado)}</p>
              </div>
              <div className="text-end">
                <InsigniaEstado estado={r.estado} />
                <div className="mt-2"><button className="btn btn-outline-primary btn-sm" type="button" onClick={() => setFolioVisible(r.codigo)}>Ver folio</button></div>
                {cancelable && <div className="mt-2">
                  {confirmando === r.codigo
                    ? <div className="border rounded p-3 mt-2 text-start" style={{ maxWidth: 430 }}>
                      <h3 className="h6">Política de cancelación</h3>
                      <ul className="small mb-2"><li>Más de 72 h: retención del 10% del total por gastos administrativos.</li><li>Entre 72 y 24 h: penalidad del 25% del total (equivalente al anticipo).</li><li>Menos de 24 h: penalidad del 100% del anticipo (o 1 SMMLV proporcional, según aplique).</li></ul>
                      <p className="small mb-1">Check-in: {formatearFecha(r.fechaEntrada)} a las 3:00 p. m. · Tramo actual: {previsualizacion?.tramo ?? 'calculando'}.</p>
                      <p className="mb-1"><strong>Penalidad a liquidar: {previsualizacion ? formatearMoneda(previsualizacion.penalidad) : 'calculando…'}</strong></p>
                      <p className="small mb-2">Anticipo requerido: {previsualizacion ? formatearMoneda(previsualizacion.anticipoRequerido) : '—'}. Pagos registrados: {previsualizacion ? formatearMoneda(previsualizacion.pagosRegistrados) : '—'}. {previsualizacion && (previsualizacion.saldoAFavorEstimado > 0 ? `Saldo a favor estimado: ${formatearMoneda(previsualizacion.saldoAFavorEstimado)}.` : `Saldo pendiente estimado: ${formatearMoneda(previsualizacion.saldoPendienteEstimado)}.`)}</p>
                      <div className="d-flex gap-2"><button className="btn btn-danger btn-sm" type="button" disabled={cancelando === r.codigo || !previsualizacion} onClick={() => void cancelar(r.codigo)}>{cancelando === r.codigo ? 'Cancelando…' : 'Confirmar cancelación'}</button>
                        <button className="btn btn-outline-secondary btn-sm" type="button" onClick={() => setConfirmando(null)}>Volver</button></div>
                    </div>
                    : <button className="btn btn-outline-danger btn-sm" type="button" disabled={preparandoCancelacion === r.codigo} onClick={() => void prepararCancelacion(r)}>{preparandoCancelacion === r.codigo ? 'Consultando folio…' : 'Cancelar reserva'}</button>}
                </div>}
              </div>
            </div>
          </div></article></div>
        })}</div>}
    <div className="mt-4"><Paginacion pagina={pagina} totalPaginas={totalPaginas} alCambiar={setPagina} /></div>
    {folioVisible && <FolioSoloLectura codigo={folioVisible} cerrar={() => setFolioVisible(null)} />}
  </main>
}

function FolioSoloLectura({ codigo, cerrar }: { codigo: string; cerrar: () => void }) {
  const [folio, setFolio] = useState<FolioResponse | null>(null)
  const [errorFolio, setErrorFolio] = useState('')
  useEffect(() => { obtenerFolio(codigo).then(setFolio).catch((e: unknown) => setErrorFolio(e instanceof Error ? e.message : 'No se pudo cargar el folio.')) }, [codigo])
  return <div className="modal d-block" role="dialog" aria-modal="true" aria-labelledby="tituloFolio" style={{ background: 'rgba(0,0,0,.45)' }}>
    <div className="modal-dialog modal-lg modal-dialog-scrollable"><div className="modal-content">
      <div className="modal-header"><h2 id="tituloFolio" className="modal-title h5">Detalle de cuenta / folio</h2><button className="btn-close" aria-label="Cerrar" onClick={cerrar} /></div>
      <div className="modal-body">{!folio && !errorFolio ? <p>Cargando folio…</p> : errorFolio ? <AlertaNotificacion mensaje={errorFolio} tipo="error" /> : folio && <>
        <p>Estado: <strong>{folio.estado === 'ABIERTO' ? 'Abierto' : 'Cerrado'}</strong></p>
        <h3 className="h6">Cargos y créditos</h3>{folio.cargos.length ? <ul className="list-group mb-3">{folio.cargos.map((c, i) => <li className="list-group-item d-flex justify-content-between" key={`${c.fechaRegistro}-${i}`}><span>{c.concepto}<small className="d-block text-secondary">{c.tipoCargo}{c.esCredito ? ' · crédito a favor' : ''} · {formatearFecha(c.fechaRegistro)}</small></span><strong>{c.esCredito ? '−' : ''}{formatearMoneda(c.monto)}</strong></li>)}</ul> : <p className="text-secondary">Sin cargos.</p>}
        <h3 className="h6">Pagos y abonos</h3>{folio.pagos.length ? <ul className="list-group mb-3">{folio.pagos.map((p, i) => <li className="list-group-item d-flex justify-content-between" key={`${p.fechaRegistro}-${i}`}><span>{p.medio.replaceAll('_', ' ')}{p.referencia ? ` · ${p.referencia}` : ''}<small className="d-block text-secondary">{formatearFecha(p.fechaRegistro)}</small></span><strong>{formatearMoneda(p.monto)}</strong></li>)}</ul> : <p className="text-secondary">Aún no hay pagos registrados.</p>}
        <hr /><p className="d-flex justify-content-between"><span>Total cargos</span><strong>{formatearMoneda(folio.totalCargos)}</strong></p><p className="d-flex justify-content-between"><span>Total pagos</span><strong>{formatearMoneda(folio.totalPagos)}</strong></p>
        <p className="d-flex justify-content-between"><span>Saldo pendiente</span><strong>{formatearMoneda(Math.max(0, folio.saldoPendiente))}</strong></p><p className="d-flex justify-content-between"><span>Saldo a favor</span><strong>{formatearMoneda(folio.saldoAFavor)}</strong></p>
      </>}</div>
      <div className="modal-footer"><button className="btn btn-secondary" onClick={cerrar}>Cerrar</button></div>
    </div></div>
  </div>
}
