import { useEffect, useState } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import type { ApartamentoResponse, CotizacionResponse } from '../../modelos/apartamento'
import type { OcupanteRequest } from '../../modelos/reserva'
import { buscarDisponibles, cotizar } from '../../servicios/apartamentoServicio'
import { crearReserva } from '../../servicios/reservaServicio'
import { esquemaNuevaReserva } from '../../esquemas/reserva'
import { formatearFecha, hoyISO, sumarDias } from '../../utilidades/fechas'
import { formatearMoneda } from '../../utilidades/formato'

const OCUPANTE_VACIO: OcupanteRequest = { nombre: '', apellido: '', documento: '', fechaNacimiento: '' }
interface Formulario { fechaEntrada: string; fechaSalida: string; horaEstimadaLlegada: string; ocupantes: OcupanteRequest[]; correo: string; telefono: string }
const FORMULARIO_VACIO: Formulario = { fechaEntrada: '', fechaSalida: '', horaEstimadaLlegada: '15:00', ocupantes: [{ ...OCUPANTE_VACIO }], correo: '', telefono: '' }

function edadEn(fechaNacimiento: string, fecha: string) {
  const nacimiento = new Date(`${fechaNacimiento}T00:00:00`)
  const destino = new Date(`${fecha}T00:00:00`)
  let edad = destino.getFullYear() - nacimiento.getFullYear()
  if (destino.getMonth() < nacimiento.getMonth() || (destino.getMonth() === nacimiento.getMonth() && destino.getDate() < nacimiento.getDate())) edad--
  return edad
}

/** Alta presencial/telefónica para recepción. El backend vuelve a validar edad, aforo y disponibilidad. */
export function NuevaReservaDirecta({ apartamentos, avisos }: { apartamentos: ApartamentoResponse[]; avisos: Avisos }) {
  const [form, setForm] = useState<Formulario>(FORMULARIO_VACIO)
  const [disponibles, setDisponibles] = useState<ApartamentoResponse[]>([])
  const [apartamentoId, setApartamentoId] = useState('')
  const [cotizacion, setCotizacion] = useState<CotizacionResponse | null>(null)
  const [error, setError] = useState('')
  const [consultando, setConsultando] = useState(false)
  const [guardando, setGuardando] = useState(false)
  const titular = form.ocupantes[0]
  const apartamentoSeleccionado = disponibles.find((a) => a.id === apartamentoId)
  const limiteOcupantes = apartamentoSeleccionado?.aforoMaximo ?? 6
  const idsCatalogo = new Set(apartamentos.map((a) => a.id))

  useEffect(() => {
    setCotizacion(null)
    if (!apartamentoId || !form.fechaEntrada || !form.fechaSalida || form.fechaSalida <= form.fechaEntrada || form.ocupantes.some((o) => !o.fechaNacimiento)) return
    let activo = true
    cotizar(apartamentoId, { fechaEntrada: form.fechaEntrada, fechaSalida: form.fechaSalida, fechasNacimiento: form.ocupantes.map((o) => o.fechaNacimiento) })
      .then((r) => { if (activo) setCotizacion(r) }).catch(() => { if (activo) setCotizacion(null) })
    return () => { activo = false }
  }, [apartamentoId, form.fechaEntrada, form.fechaSalida, form.ocupantes])

  const cambiar = <K extends keyof Formulario>(clave: K, valor: Formulario[K]) => setForm((actual) => ({ ...actual, [clave]: valor }))
  const cambiarOcupante = (i: number, clave: keyof OcupanteRequest, valor: string) => {
    const ocupantes = form.ocupantes.map((o, index) => index === i ? { ...o, [clave]: valor } : o)
    setForm((actual) => ({ ...actual, ocupantes }))
    setDisponibles([]); setApartamentoId('')
  }

  const consultarDisponibilidad = async () => {
    setError(''); setCotizacion(null); setApartamentoId('')
    const validacion = esquemaNuevaReserva.safeParse({ fechaEntrada: form.fechaEntrada, fechaSalida: form.fechaSalida, horaEstimadaLlegada: form.horaEstimadaLlegada, ocupantes: form.ocupantes })
    if (!validacion.success) { setError(validacion.error.issues[0]?.message ?? 'Revisa los datos de la reserva.'); return }
    if (!form.correo.trim() || !/^\S+@\S+\.\S+$/.test(form.correo)) { setError('Indica un correo válido para el titular.'); return }
    if (!form.telefono.trim()) { setError('Indica un teléfono de contacto del titular.'); return }
    if (edadEn(titular.fechaNacimiento, form.fechaEntrada) < 18) { setError('El titular debe tener al menos 18 años al momento del check-in.'); return }
    setConsultando(true)
    try {
      const resultado = await buscarDisponibles({ fechaEntrada: form.fechaEntrada, fechaSalida: form.fechaSalida, fechasNacimiento: form.ocupantes.map((o) => o.fechaNacimiento) })
      const lista = resultado.contenido.filter((a) => idsCatalogo.has(a.id) && form.ocupantes.length <= a.aforoMaximo)
      setDisponibles(lista)
      if (!lista.length) setError('No hay apartamentos disponibles para esas fechas y ocupación.')
    } catch (causa) { setError(causa instanceof Error ? causa.message : 'No se pudo consultar disponibilidad.') } finally { setConsultando(false) }
  }

  const enviar = async () => {
    setError('')
    if (!apartamentoId || !cotizacion) { setError('Consulta disponibilidad y selecciona un apartamento antes de crear la reserva.'); return }
    setGuardando(true)
    try {
      const resultado = await crearReserva({
        apartamentoId, nombreTitular: titular.nombre, apellidoTitular: titular.apellido,
        correoTitular: form.correo, telefonoTitular: form.telefono, documentoTitular: titular.documento,
        ocupantes: form.ocupantes, fechaEntrada: form.fechaEntrada, fechaSalida: form.fechaSalida,
        horaEstimadaLlegada: form.horaEstimadaLlegada, canalOrigen: 'DIRECTO',
      })
      avisos.exito(`Reserva directa creada (${resultado.estado}). Total ${formatearMoneda(resultado.valorCongelado)}; anticipo 25%: ${formatearMoneda(Math.round(resultado.valorCongelado * 0.25))}.`)
      setForm(FORMULARIO_VACIO); setDisponibles([]); setApartamentoId(''); setCotizacion(null)
    } catch (causa) { setError(causa instanceof Error ? causa.message : 'No fue posible crear la reserva.') } finally { setGuardando(false) }
  }

  return <section className="card shadow-sm"><div className="card-body">
    <h2 className="h5">Nueva reserva directa</h2><p className="small text-secondary">Canal presencial o telefónico. El titular debe ser mayor de edad y está incluido en los ocupantes.</p>
    {error && <div className="alert alert-danger" role="alert">{error}</div>}
    <div className="row g-3">
      <div className="col-12 col-md-4"><label className="form-label" htmlFor="directaEntrada">Entrada</label><input id="directaEntrada" type="date" min={hoyISO()} className="form-control" value={form.fechaEntrada} onChange={(e) => { cambiar('fechaEntrada', e.target.value); setDisponibles([]); setApartamentoId('') }} /></div>
      <div className="col-12 col-md-4"><label className="form-label" htmlFor="directaSalida">Salida</label><input id="directaSalida" type="date" min={form.fechaEntrada ? sumarDias(form.fechaEntrada, 1) : sumarDias(hoyISO(), 1)} className="form-control" value={form.fechaSalida} onChange={(e) => { cambiar('fechaSalida', e.target.value); setDisponibles([]); setApartamentoId('') }} /></div>
      <div className="col-12 col-md-4"><label className="form-label" htmlFor="directaHora">Hora estimada de llegada</label><input id="directaHora" type="time" className="form-control" value={form.horaEstimadaLlegada} onChange={(e) => cambiar('horaEstimadaLlegada', e.target.value)} /></div>
      <div className="col-12 col-md-6"><label className="form-label" htmlFor="titularCorreo">Correo del titular</label><input id="titularCorreo" type="email" className="form-control" value={form.correo} onChange={(e) => cambiar('correo', e.target.value)} /></div>
      <div className="col-12 col-md-6"><label className="form-label" htmlFor="titularTelefono">Teléfono del titular</label><input id="titularTelefono" className="form-control" value={form.telefono} onChange={(e) => cambiar('telefono', e.target.value)} /></div>
    </div>
    <div className="d-flex justify-content-between align-items-center mt-4 mb-2"><h3 className="h6 mb-0">Titular y acompañantes ({form.ocupantes.length})</h3>
      <button type="button" className="btn btn-outline-primary btn-sm" disabled={form.ocupantes.length >= limiteOcupantes} onClick={() => cambiar('ocupantes', [...form.ocupantes, { ...OCUPANTE_VACIO }])}>Agregar acompañante</button></div>
    {form.ocupantes.map((o, i) => <div className="row g-2 border rounded p-2 mx-0 mb-2" key={i}>
      <strong className="small">{i === 0 ? 'Titular' : `Acompañante ${i}`}</strong>
      {([['nombre','Nombre','text'],['apellido','Apellido','text'],['documento','Documento','text'],['fechaNacimiento','Fecha de nacimiento','date']] as const).map(([k, label, type]) => <div className="col-12 col-md-3" key={k}>
        <label className="form-label small" htmlFor={`directa-${i}-${k}`}>{label}</label><input id={`directa-${i}-${k}`} type={type} max={type === 'date' ? hoyISO() : undefined} className="form-control" value={o[k]} onChange={(e) => cambiarOcupante(i, k, e.target.value)} /></div>)}
      {i > 0 && <div className="col-12 text-end"><button type="button" className="btn btn-link btn-sm text-danger" onClick={() => cambiar('ocupantes', form.ocupantes.filter((_, n) => n !== i))}>Quitar acompañante</button></div>}
    </div>)}
    <p className="small text-secondary">Los menores de 5 años cuentan para el aforo, pero no pagan tarifa adicional. A partir de los 5 años cumplidos son facturables.</p>
    <button className="btn btn-outline-primary" type="button" disabled={consultando} onClick={() => void consultarDisponibilidad()}>{consultando ? 'Consultando…' : 'Consultar disponibilidad'}</button>
    {disponibles.length > 0 && <div className="mt-3"><label className="form-label" htmlFor="aptoDirecto">Apartamento disponible</label><select id="aptoDirecto" className="form-select" value={apartamentoId} onChange={(e) => setApartamentoId(e.target.value)}><option value="">Selecciona…</option>{disponibles.map((a) => <option key={a.id} value={a.id}>Apartamento {a.identificadorUnico} · {a.categoria === 'PREMIUM' ? 'Premium' : 'Standard'} · máximo {a.aforoMaximo}</option>)}</select></div>}
    {cotizacion && <div className="alert alert-light border mt-3"><h3 className="h6">Cotización · {formatearFecha(form.fechaEntrada)} – {formatearFecha(form.fechaSalida)}</h3>
      <p className="small">{apartamentoSeleccionado?.categoria === 'PREMIUM' ? `Tarifa por persona facturable (${cotizacion.ocupantesFacturables} persona(s) facturable(s)) y noche.` : 'Tarifa fija por apartamento y noche.'}</p>
      <ul className="small mb-2">{cotizacion.lineas.map((l) => <li key={l.noche}>{formatearFecha(l.noche)} · {l.temporada}: {formatearMoneda(l.subtotal)}</li>)}</ul>
      <p className="mb-1"><strong>Total: {formatearMoneda(cotizacion.total)}</strong></p><p className="mb-1">Anticipo requerido (25%): <strong>{formatearMoneda(Math.round(cotizacion.total * 0.25))}</strong></p>
      <p className="small mb-0">Plazo para adjuntar comprobante: 4 horas. Medios aceptados: efectivo, tarjeta débito/crédito y transferencia bancaria.</p>
    </div>}
    <button className="btn btn-success mt-3" type="button" disabled={guardando || !cotizacion} onClick={() => void enviar()}>{guardando ? 'Creando…' : 'Crear reserva directa'}</button>
  </div></section>
}
