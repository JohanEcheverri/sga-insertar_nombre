import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaCargo, esquemaCierreFolio, esquemaPago } from '../../esquemas/folio'
import type { FolioResponse } from '../../modelos/folio'
import { MEDIOS_PAGO, TIPOS_CARGO_MANUAL, TIPOS_CREDITO } from '../../modelos/folio'
import { cerrarFolio, obtenerFolio, registrarCargo, registrarPago } from '../../servicios/folioServicio'
import { formatearFechaHora } from '../../utilidades/fechas'
import { etiqueta, formatearMoneda } from '../../utilidades/formato'
import { validar } from '../../utilidades/validacion'
import { InsigniaEstado } from '../InsigniaEstado'
import { useAuth } from '../../contexto/useAuth'

interface Props {
  codigoInicial: string
  avisos: Avisos
}

type Errores = Record<string, string>
const MensajeError = ({ texto }: { texto?: string }) => (texto ? <div className="text-danger small">{texto}</div> : null)

/** Folio de una reserva: cargos, pagos parciales, cargos manuales y cierre (RN-15, RN-16, RN-17). */
export function PanelFolio({ codigoInicial, avisos }: Props) {
  const { tieneRol } = useAuth()
  const esAdmin = tieneRol(['ADMINISTRADOR'])
  const [codigo, setCodigo] = useState(codigoInicial)
  const [folio, setFolio] = useState<FolioResponse | null>(null)
  const [cargando, setCargando] = useState(false)
  const [pago, setPago] = useState({ monto: '', medio: 'EFECTIVO', referencia: '' })
  const [cargo, setCargo] = useState({ tipoCargo: 'SERVICIO_ADICIONAL', concepto: '', monto: '' })
  const [motivo, setMotivo] = useState('Cierre ordinario')
  const [autorizaSaldoPendiente, setAutorizaSaldoPendiente] = useState(false)
  const [errPago, setErrPago] = useState<Errores>({})
  const [errCargo, setErrCargo] = useState<Errores>({})
  const [errCierre, setErrCierre] = useState<Errores>({})
  const { exito, fallo } = avisos

  const cargar = useCallback(async (cod: string) => {
    if (!cod.trim()) return
    setCargando(true)
    try { setFolio(await obtenerFolio(cod.trim())) } catch (causa) { setFolio(null); fallo(causa) } finally { setCargando(false) }
  }, [fallo])

  useEffect(() => { if (codigoInicial) { setCodigo(codigoInicial); void cargar(codigoInicial) } }, [codigoInicial, cargar])

  const abierto = folio?.estado === 'ABIERTO'

  const enviarPago = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores } = validar(esquemaPago, pago)
    setErrPago(errores)
    if (!datos || !folio) return
    try {
      setFolio(await registrarPago(codigo.trim(), { monto: datos.monto, medio: datos.medio, referencia: datos.referencia || undefined }))
      setPago({ monto: '', medio: 'EFECTIVO', referencia: '' })
      exito('Pago registrado.')
    } catch (causa) { fallo(causa) }
  }

  const enviarCargo = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores } = validar(esquemaCargo, cargo)
    setErrCargo(errores)
    if (!datos || !folio) return
    try {
      setFolio(await registrarCargo(codigo.trim(), datos))
      setCargo({ tipoCargo: 'SERVICIO_ADICIONAL', concepto: '', monto: '' })
      exito('Cargo registrado. Los cargos no se editan: las correcciones se hacen con un ajuste a favor.')
    } catch (causa) { fallo(causa) }
  }

  const enviarCierre = async (e: FormEvent) => {
    e.preventDefault()
    if (folio && folio.saldoPendiente > 0 && (!esAdmin || !autorizaSaldoPendiente)) return
    const { datos, errores } = validar(esquemaCierreFolio, { motivo })
    setErrCierre(errores)
    if (!datos || !folio) return
    try {
      setFolio(await cerrarFolio(codigo.trim(), datos.motivo))
      exito('Folio cerrado.')
    } catch (causa) { fallo(causa) }
  }

  return <section>
    <form className="d-flex gap-2 mb-3" onSubmit={(e) => { e.preventDefault(); void cargar(codigo) }}>
      <input className="form-control" placeholder="Código de la reserva (UUID)" aria-label="Código de la reserva" value={codigo} onChange={(e) => setCodigo(e.target.value)} />
      <button className="btn btn-primary" type="submit" disabled={cargando}>{cargando ? 'Buscando…' : 'Consultar'}</button>
    </form>

    {folio && <>
      <div className="row g-3 mb-3">
        <div className="col-6 col-md-3"><div className="card"><div className="card-body"><div className="text-secondary small">Estado</div><InsigniaEstado estado={folio.estado} /></div></div></div>
        <div className="col-6 col-md-3"><div className="card"><div className="card-body"><div className="text-secondary small">Cargos</div><strong>{formatearMoneda(folio.totalCargos)}</strong></div></div></div>
        <div className="col-6 col-md-3"><div className="card"><div className="card-body"><div className="text-secondary small">Pagos ({folio.cantidadPagos})</div><strong>{formatearMoneda(folio.totalPagos)}</strong></div></div></div>
        <div className="col-6 col-md-3"><div className="card"><div className="card-body"><div className="text-secondary small">{folio.saldoPendiente < 0 ? 'Saldo a favor' : 'Saldo'}</div>
          <strong className={folio.saldoPendiente > 0 ? 'text-danger' : 'text-success'}>{formatearMoneda(Math.abs(folio.saldoPendiente))}</strong></div></div></div>
      </div>

      <div className="table-responsive mb-3"><table className="table table-sm">
        <thead><tr><th>Fecha</th><th>Tipo</th><th>Concepto</th><th className="text-end">Monto</th></tr></thead>
        <tbody>{folio.cargos.map((c, i) => { const credito = TIPOS_CREDITO.includes(c.tipoCargo); return <tr key={`${c.fechaRegistro}-${i}`}>
          <td>{formatearFechaHora(c.fechaRegistro)}</td><td>{etiqueta(c.tipoCargo)}</td><td>{c.concepto}</td>
          <td className={`text-end${credito ? ' text-success' : ''}`}>{credito ? '−' : ''}{formatearMoneda(c.monto)}</td></tr> })}</tbody>
      </table></div>
      {folio.autorizacionCierre && <p className="small text-secondary">Cierre registrado por {folio.autorizacionCierre.autor} el {formatearFechaHora(folio.autorizacionCierre.fecha)}: {folio.autorizacionCierre.motivo}</p>}

      {abierto ? <div className="row g-4">
        <form className="col-12 col-lg-4" onSubmit={enviarPago} noValidate><h3 className="h6">Registrar pago</h3>
          <input className="form-control mb-1" type="number" placeholder="Monto (COP)" aria-label="Monto del pago" value={pago.monto} onChange={(e) => setPago({ ...pago, monto: e.target.value })} /><MensajeError texto={errPago.monto} />
          <select className="form-select my-1" aria-label="Medio de pago" value={pago.medio} onChange={(e) => setPago({ ...pago, medio: e.target.value })}>{MEDIOS_PAGO.map((m) => <option key={m} value={m}>{etiqueta(m)}</option>)}</select><MensajeError texto={errPago.medio} />
          <input className="form-control my-1" placeholder="Referencia (opcional)" aria-label="Referencia" value={pago.referencia} onChange={(e) => setPago({ ...pago, referencia: e.target.value })} />
          <button className="btn btn-success w-100 mt-1" type="submit">Registrar pago</button></form>
        <form className="col-12 col-lg-4" onSubmit={enviarCargo} noValidate><h3 className="h6">Registrar cargo manual</h3>
          <select className="form-select mb-1" aria-label="Tipo de cargo" value={cargo.tipoCargo} onChange={(e) => setCargo({ ...cargo, tipoCargo: e.target.value })}>{TIPOS_CARGO_MANUAL.map((t) => <option key={t} value={t}>{etiqueta(t)}</option>)}</select><MensajeError texto={errCargo.tipoCargo} />
          <input className="form-control my-1" placeholder="Concepto" aria-label="Concepto" value={cargo.concepto} onChange={(e) => setCargo({ ...cargo, concepto: e.target.value })} /><MensajeError texto={errCargo.concepto} />
          <input className="form-control my-1" type="number" placeholder="Monto (COP)" aria-label="Monto del cargo" value={cargo.monto} onChange={(e) => setCargo({ ...cargo, monto: e.target.value })} /><MensajeError texto={errCargo.monto} />
          <button className="btn btn-primary w-100 mt-1" type="submit">Registrar cargo</button></form>
        <form className="col-12 col-lg-4" onSubmit={enviarCierre} noValidate><h3 className="h6">{folio.saldoPendiente > 0 ? 'Cierre excepcional con saldo' : 'Cerrar folio'}</h3>
          <input className="form-control mb-1" aria-label="Motivo del cierre" value={motivo} onChange={(e) => setMotivo(e.target.value)} /><MensajeError texto={errCierre.motivo} />
          {folio.saldoPendiente > 0 ? <>
            <p className="form-text">Saldo pendiente: <strong>{formatearMoneda(folio.saldoPendiente)}</strong>. La excepción requiere autorización administrativa y una justificación; ambas quedarán en la auditoría del folio.</p>
            {esAdmin ? <label className="form-check mb-2"><input className="form-check-input" type="checkbox" checked={autorizaSaldoPendiente} onChange={(e) => setAutorizaSaldoPendiente(e.target.checked)} /><span className="form-check-label">Autorizo explícitamente cerrar este folio con saldo pendiente</span></label> : <p className="small text-danger">Solo un administrador puede autorizar esta excepción.</p>}
          </> : <p className="form-text">El folio debe estar en cero para el cierre ordinario.</p>}
          <button className="btn btn-dark w-100" type="submit" disabled={folio.saldoPendiente > 0 && (!esAdmin || !autorizaSaldoPendiente)}>{folio.saldoPendiente > 0 ? 'Autorizar cierre excepcional' : 'Cerrar folio'}</button></form>
      </div> : <p className="text-secondary">El folio está cerrado: no admite más cargos ni pagos.</p>}
    </>}
  </section>
}
