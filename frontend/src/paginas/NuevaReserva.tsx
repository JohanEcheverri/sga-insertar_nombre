import { useEffect, useState } from 'react'
import { useFieldArray, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { esquemaNuevaReserva } from '../esquemas/reserva'
import type { FormularioNuevaReserva } from '../esquemas/reserva'
import type { ApartamentoResponse, CotizacionResponse } from '../modelos/apartamento'
import type { PerfilUsuario } from '../modelos/perfil'
import { useAuth } from '../contexto/useAuth'
import { cotizar, obtenerApartamento } from '../servicios/apartamentoServicio'
import { crearReserva } from '../servicios/reservaServicio'
import { obtenerUsuario } from '../servicios/usuarioServicio'
import { extraerMensajeError } from '../utilidades/errores'
import { formatearFecha, hoyISO, nochesEntre, sumarDias } from '../utilidades/fechas'
import { formatearMoneda } from '../utilidades/formato'
import { obtenerUsuarioId } from '../utilidades/jwt'

const OCUPANTE_VACIO = { nombre: '', apellido: '', documento: '', fechaNacimiento: '' }
const FECHA_VALIDA = /^\d{4}-\d{2}-\d{2}$/

export default function NuevaReserva() {
  const { id } = useParams()
  if (!id) return <main className="container py-5"><AlertaNotificacion mensaje="Primero elige un apartamento en el catálogo." /><Link to="/">Ir al catálogo</Link></main>
  return <FormularioReserva id={id} />
}

function FormularioReserva({ id }: { id: string }) {
  const [params] = useSearchParams()
  const navegar = useNavigate()
  const { sesion } = useAuth()
  const usuarioId = obtenerUsuarioId(sesion?.token)
  const [apartamento, setApartamento] = useState<ApartamentoResponse | null>(null)
  const [perfil, setPerfil] = useState<PerfilUsuario | null>(null)
  const [cotizacion, setCotizacion] = useState<CotizacionResponse | null>(null)
  const [errorCotizacion, setErrorCotizacion] = useState('')
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  const { register, control, handleSubmit, reset, watch, formState: { errors, isSubmitting } } = useForm<FormularioNuevaReserva>({
    resolver: zodResolver(esquemaNuevaReserva),
    defaultValues: { fechaEntrada: params.get('entrada') ?? '', fechaSalida: params.get('salida') ?? '', horaEstimadaLlegada: '15:00', ocupantes: [OCUPANTE_VACIO] },
  })
  const { fields, append, remove } = useFieldArray({ control, name: 'ocupantes' })

  // Carga el apartamento y el perfil: el titular es siempre el primer ocupante (RN-23).
  useEffect(() => {
    let activo = true
    async function cargar() {
      try {
        if (!usuarioId) throw new Error('No pudimos identificar tu sesión. Vuelve a iniciar sesión.')
        const [apto, usuario] = await Promise.all([obtenerApartamento(id), obtenerUsuario(usuarioId)])
        if (!activo) return
        setApartamento(apto)
        setPerfil(usuario)
        const adultos = Number(params.get('adultos') ?? 1)
        const menores = Number(params.get('menores') ?? 0)
        const personasSolicitadas = Number.isFinite(adultos + menores) ? Math.max(1, Math.min(40, adultos + menores)) : 1
        const extras = Array.from({ length: personasSolicitadas - 1 }, () => ({ ...OCUPANTE_VACIO }))
        reset({
          fechaEntrada: params.get('entrada') ?? '', fechaSalida: params.get('salida') ?? '', horaEstimadaLlegada: '15:00',
          ocupantes: [{ nombre: usuario.nombre, apellido: usuario.apellido, documento: usuario.documento, fechaNacimiento: usuario.fechaNacimiento }, ...extras],
        })
      } catch (causa) {
        if (activo) setError(extraerMensajeError(causa))
      } finally {
        if (activo) setCargando(false)
      }
    }
    void cargar()
    return () => { activo = false }
  }, [id, usuarioId, params, reset])

  // Cotización en vivo al cambiar fechas u ocupantes.
  const [entrada, salida, ocupantes] = watch(['fechaEntrada', 'fechaSalida', 'ocupantes'])
  const clave = JSON.stringify([entrada, salida, ocupantes.map((o) => o.fechaNacimiento)])
  useEffect(() => {
    const nacimientos = ocupantes.map((o) => o.fechaNacimiento)
    const valido = FECHA_VALIDA.test(entrada) && FECHA_VALIDA.test(salida) && salida > entrada && nacimientos.every((f) => FECHA_VALIDA.test(f))
    if (!valido || !apartamento) { setCotizacion(null); setErrorCotizacion(''); return }
    let activo = true
    const temporizador = setTimeout(() => {
      cotizar(apartamento.id, { fechaEntrada: entrada, fechaSalida: salida, fechasNacimiento: nacimientos })
        .then((c) => { if (activo) { setCotizacion(c); setErrorCotizacion('') } })
        .catch((causa: unknown) => { if (activo) { setCotizacion(null); setErrorCotizacion(extraerMensajeError(causa)) } })
    }, 400)
    return () => { activo = false; clearTimeout(temporizador) }
  }, [clave, apartamento])

  const enviar = async (valores: FormularioNuevaReserva) => {
    setError('')
    if (!apartamento || !perfil) return
    if (valores.ocupantes.length > apartamento.aforoMaximo) {
      setError(`Este apartamento admite máximo ${apartamento.aforoMaximo} personas.`)
      return
    }
    try {
      const reserva = await crearReserva({
        apartamentoId: apartamento.id, nombreTitular: perfil.nombre, apellidoTitular: perfil.apellido, usuarioId: perfil.id,
        correoTitular: perfil.correo, telefonoTitular: perfil.telefono, documentoTitular: perfil.documento,
        ocupantes: valores.ocupantes, fechaEntrada: valores.fechaEntrada, fechaSalida: valores.fechaSalida,
        horaEstimadaLlegada: valores.horaEstimadaLlegada, canalOrigen: 'PORTAL',
      })
      navegar('/mis-reservas', { replace: true, state: { mensaje: `Reserva creada en estado ${reserva.estado}. Valor: ${formatearMoneda(reserva.valorCongelado)}. Recuerda cubrir el anticipo para que sea confirmada.` } })
    } catch (causa) {
      setError(extraerMensajeError(causa))
    }
  }

  if (cargando) return <main className="container py-5"><p className="text-secondary">Preparando tu reserva…</p></main>
  if (!apartamento || !perfil) return <main className="container py-5"><AlertaNotificacion mensaje={error || 'No fue posible cargar la reserva.'} tipo="error" /><Link to="/">Volver al catálogo</Link></main>

  const campo = (nombre: 'nombre' | 'apellido' | 'documento' | 'fechaNacimiento', i: number) => errors.ocupantes?.[i]?.[nombre]?.message

  return <main className="container py-4">
    <Link to={`/apartamentos/${id}`} className="small">← Volver al detalle</Link>
    <h1 className="h3 mt-2">Reservar apartamento {apartamento.identificadorUnico}</h1>
    <p className="text-secondary">Capacidad máxima: {apartamento.aforoMaximo} personas. Titular: {perfil.nombre} {perfil.apellido}. La reserva nace PENDIENTE y se confirma al cubrir el anticipo.</p>
    <AlertaNotificacion mensaje={error} tipo="error" />

    <form onSubmit={handleSubmit(enviar)} noValidate>
      <div className="row g-4">
        <div className="col-12 col-lg-8">
          <section className="card shadow-sm mb-4"><div className="card-body"><h2 className="h5">Estancia</h2>
            <div className="row g-3">
              <div className="col-12 col-md-4"><label className="form-label" htmlFor="fechaEntrada">Entrada</label>
                <input id="fechaEntrada" type="date" min={hoyISO()} className={`form-control${errors.fechaEntrada ? ' is-invalid' : ''}`} {...register('fechaEntrada')} />
                {errors.fechaEntrada && <div className="invalid-feedback">{errors.fechaEntrada.message}</div>}</div>
              <div className="col-12 col-md-4"><label className="form-label" htmlFor="fechaSalida">Salida</label>
                <input id="fechaSalida" type="date" min={entrada ? sumarDias(entrada, 1) : sumarDias(hoyISO(), 1)} className={`form-control${errors.fechaSalida ? ' is-invalid' : ''}`} {...register('fechaSalida')} />
                {errors.fechaSalida && <div className="invalid-feedback">{errors.fechaSalida.message}</div>}</div>
              <div className="col-12 col-md-4"><label className="form-label" htmlFor="hora">Hora estimada de llegada</label>
                <input id="hora" type="time" className={`form-control${errors.horaEstimadaLlegada ? ' is-invalid' : ''}`} {...register('horaEstimadaLlegada')} />
                {errors.horaEstimadaLlegada && <div className="invalid-feedback">{errors.horaEstimadaLlegada.message}</div>}</div>
            </div></div></section>

          <section className="card shadow-sm"><div className="card-body">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h2 className="h5 mb-0">Ocupantes ({fields.length}/{apartamento.aforoMaximo})</h2>
              <button type="button" className="btn btn-outline-primary btn-sm" disabled={fields.length >= apartamento.aforoMaximo} onClick={() => append({ ...OCUPANTE_VACIO })}>Agregar ocupante</button>
            </div>
            {fields.length > apartamento.aforoMaximo && <AlertaNotificacion mensaje={`El grupo solicitado supera el aforo de ${apartamento.aforoMaximo}. Quita ocupantes para continuar.`} tipo="error" />}
            {errors.ocupantes?.message && <div className="text-danger small mb-2">{errors.ocupantes.message}</div>}
            {fields.map((campoOcupante, i) => <div className="border rounded p-3 mb-3" key={campoOcupante.id}>
              <div className="d-flex justify-content-between mb-2">
                <strong>{i === 0 ? 'Titular (tú)' : `Ocupante ${i + 1}`}</strong>
                {i > 0 && <button type="button" className="btn btn-link btn-sm text-danger p-0" onClick={() => remove(i)}>Quitar</button>}
              </div>
              <div className="row g-2">
                {([['nombre', 'Nombre', 'text'], ['apellido', 'Apellido', 'text'], ['documento', 'Documento', 'text'], ['fechaNacimiento', 'Fecha de nacimiento', 'date']] as const).map(([nombre, etiqueta, tipo]) =>
                  <div className="col-12 col-md-6" key={nombre}>
                    <label className="form-label small" htmlFor={`o${i}${nombre}`}>{etiqueta}</label>
                    <input id={`o${i}${nombre}`} type={tipo} max={nombre === 'fechaNacimiento' ? hoyISO() : undefined} readOnly={i === 0} className={`form-control${campo(nombre, i) ? ' is-invalid' : ''}`} {...register(`ocupantes.${i}.${nombre}` as const)} />
                    {campo(nombre, i) && <div className="invalid-feedback">{campo(nombre, i)}</div>}
                  </div>)}
              </div>
            </div>)}
            <p className="form-text mb-0">La fecha de nacimiento define quién genera cargo: los menores del umbral de edad ocupan cupo pero no se cobran.</p>
          </div></section>
        </div>

        <aside className="col-12 col-lg-4"><section className="card shadow-sm position-sticky" style={{ top: 16 }}><div className="card-body">
          <h2 className="h5">Resumen</h2>
          {errorCotizacion && <AlertaNotificacion mensaje={errorCotizacion} tipo="error" />}
          {cotizacion ? <>
            <p className="mb-1">{formatearFecha(entrada)} → {formatearFecha(salida)}</p>
            <p className="text-secondary small">{nochesEntre(entrada, salida)} noches · {cotizacion.ocupantesFacturables} ocupante(s) facturable(s)</p>
            <ul className="list-unstyled small">{cotizacion.lineas.map((l) => <li className="d-flex justify-content-between" key={l.noche}><span>{formatearFecha(l.noche)} · {l.temporada}</span><span>{formatearMoneda(l.subtotal)}</span></li>)}</ul>
            <hr /><p className="d-flex justify-content-between fw-bold fs-5 mb-3"><span>Total</span><span>{formatearMoneda(cotizacion.total)}</span></p>
          </> : <p className="text-secondary small">Completa fechas y ocupantes para ver el valor.</p>}
          <button className="btn btn-success w-100" type="submit" disabled={isSubmitting}>{isSubmitting ? 'Creando reserva…' : 'Crear reserva'}</button>
        </div></section></aside>
      </div>
    </form>
  </main>
}
