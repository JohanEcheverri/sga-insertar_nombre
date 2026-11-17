import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useParams, useSearchParams } from 'react-router'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { InsigniaEstado } from '../componentes/InsigniaEstado'
import { esquemaBusqueda } from '../esquemas/apartamento'
import type { ApartamentoResponse, CotizacionResponse } from '../modelos/apartamento'
import { useAuth } from '../contexto/useAuth'
import { apartamentoDisponible, cotizar, obtenerApartamento } from '../servicios/apartamentoServicio'
import { extraerMensajeError } from '../utilidades/errores'
import { fechasNacimientoGrupo, formatearFecha, hoyISO, nochesEntre, sumarDias } from '../utilidades/fechas'
import { formatearMoneda } from '../utilidades/formato'
import { validar } from '../utilidades/validacion'

interface Resultado { disponible: boolean; cotizacion: CotizacionResponse | null }

export default function DetalleApartamento() {
  const { id = '' } = useParams()
  const [params] = useSearchParams()
  const { estaAutenticado, tieneRol } = useAuth()
  const [apartamento, setApartamento] = useState<ApartamentoResponse | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [entrada, setEntrada] = useState(params.get('entrada') ?? '')
  const [salida, setSalida] = useState(params.get('salida') ?? '')
  const [adultos, setAdultos] = useState(params.get('adultos') ?? '1')
  const [menores, setMenores] = useState(params.get('menores') ?? '0')
  const [errores, setErrores] = useState<Record<string, string>>({})
  const [consultando, setConsultando] = useState(false)
  const [resultado, setResultado] = useState<Resultado | null>(null)
  const [errorConsulta, setErrorConsulta] = useState('')

  useEffect(() => {
    let activo = true
    obtenerApartamento(id)
      .then((a) => { if (activo) setApartamento(a) })
      .catch((causa: unknown) => { if (activo) setError(extraerMensajeError(causa)) })
      .finally(() => { if (activo) setCargando(false) })
    return () => { activo = false }
  }, [id])

  const consultar = async (evento: FormEvent) => {
    evento.preventDefault()
    setErrorConsulta('')
    setResultado(null)
    const { datos, errores: errs } = validar(esquemaBusqueda, {
      fechaEntrada: entrada, fechaSalida: salida, adultos: Number(adultos), menores: Number(menores), precioMaximo: '',
    })
    if (datos && (!entrada || !salida)) errs.fechaEntrada = 'Indica las fechas de tu estancia.'
    setErrores(errs)
    if (!datos || Object.keys(errs).length > 0) return
    if (apartamento && datos.adultos + datos.menores > apartamento.aforoMaximo) {
      setErrorConsulta(`Este apartamento admite máximo ${apartamento.aforoMaximo} personas.`)
      return
    }
    const grupo = { fechaEntrada: entrada, fechaSalida: salida, fechasNacimiento: fechasNacimientoGrupo(datos.adultos, datos.menores) }
    setConsultando(true)
    try {
      const disponible = await apartamentoDisponible(id, grupo)
      setResultado({ disponible, cotizacion: await cotizar(id, grupo) })
    } catch (causa) {
      setErrorConsulta(extraerMensajeError(causa))
    } finally {
      setConsultando(false)
    }
  }

  if (cargando) return <main className="container py-5"><p className="text-secondary">Cargando apartamento…</p></main>
  if (!apartamento) return <main className="container py-5"><AlertaNotificacion mensaje={error || 'No encontramos el apartamento.'} tipo="error" /><Link to="/">Volver al catálogo</Link></main>

  const consulta = new URLSearchParams({ entrada, salida, adultos, menores }).toString()
  const puedeReservar = estaAutenticado && tieneRol(['HUESPED'])

  return <main className="container py-4">
    <Link to="/" className="small">← Volver al catálogo</Link>
    <div className="row g-4 mt-1">
      <div className="col-12 col-lg-7">
        <div className="bg-secondary-subtle rounded-4 d-flex align-items-center justify-content-center mb-3" style={{ height: 220 }}>
          <span className="display-3 fw-bold text-secondary">{apartamento.identificadorUnico}</span>
        </div>
        <h1 className="h3">Apartamento {apartamento.identificadorUnico}</h1>
        <p>{apartamento.descripcion}</p>
        <p className="mb-2">Capacidad máxima: <strong>{apartamento.aforoMaximo} personas</strong> · Estado operativo: <InsigniaEstado estado={apartamento.estadoOperativo} /></p>
        <h2 className="h6 mt-4">Dormitorios</h2>
        <ul>{apartamento.dormitorios.map((d) => <li key={d}>{d}</li>)}</ul>
        <p className="text-secondary small">El apartamento se entrega completo y en exclusiva a tu grupo. Se cobra por persona facturable y por noche; los menores del umbral de edad ocupan cupo sin generar cargo.</p>
      </div>

      <div className="col-12 col-lg-5">
        <section className="card shadow-sm"><div className="card-body">
          <h2 className="h5">Consulta tu estancia</h2>
          <form onSubmit={consultar} noValidate>
            <div className="row g-2">
              <div className="col-6"><label className="form-label" htmlFor="entrada">Entrada</label>
                <input id="entrada" type="date" min={hoyISO()} className={`form-control${errores.fechaEntrada ? ' is-invalid' : ''}`} value={entrada} onChange={(e) => setEntrada(e.target.value)} />
                {errores.fechaEntrada && <div className="invalid-feedback">{errores.fechaEntrada}</div>}</div>
              <div className="col-6"><label className="form-label" htmlFor="salida">Salida</label>
                <input id="salida" type="date" min={entrada ? sumarDias(entrada, 1) : hoyISO()} className={`form-control${errores.fechaSalida ? ' is-invalid' : ''}`} value={salida} onChange={(e) => setSalida(e.target.value)} />
                {errores.fechaSalida && <div className="invalid-feedback">{errores.fechaSalida}</div>}</div>
              <div className="col-6"><label className="form-label" htmlFor="adultos">Adultos</label>
                <input id="adultos" type="number" min={1} className={`form-control${errores.adultos ? ' is-invalid' : ''}`} value={adultos} onChange={(e) => setAdultos(e.target.value)} />
                {errores.adultos && <div className="invalid-feedback">{errores.adultos}</div>}</div>
              <div className="col-6"><label className="form-label" htmlFor="menores">Menores</label>
                <input id="menores" type="number" min={0} className={`form-control${errores.menores ? ' is-invalid' : ''}`} value={menores} onChange={(e) => setMenores(e.target.value)} />
                {errores.menores && <div className="invalid-feedback">{errores.menores}</div>}</div>
            </div>
            <button className="btn btn-primary w-100 mt-3" type="submit" disabled={consultando}>{consultando ? 'Consultando…' : 'Ver disponibilidad y precio'}</button>
          </form>
          <div className="mt-3"><AlertaNotificacion mensaje={errorConsulta} tipo="error" /></div>

          {resultado && <div className="mt-3">
            {resultado.disponible
              ? <AlertaNotificacion tipo="exito" mensaje={`Disponible del ${formatearFecha(entrada)} al ${formatearFecha(salida)} (${nochesEntre(entrada, salida)} noches).`} />
              : <AlertaNotificacion tipo="error" mensaje="No está disponible para esas fechas. Prueba con otras fechas." />}
            {resultado.cotizacion && <>
              <div className="table-responsive"><table className="table table-sm">
                <thead><tr><th>Noche</th><th>Temporada</th><th className="text-end">Tarifa</th><th className="text-end">Pers.</th><th className="text-end">Subtotal</th></tr></thead>
                <tbody>{resultado.cotizacion.lineas.map((l) => <tr key={l.noche}>
                  <td>{formatearFecha(l.noche)}</td><td>{l.temporada}</td>
                  <td className="text-end">{formatearMoneda(l.tarifa)}</td><td className="text-end">{l.ocupantesFacturables}</td>
                  <td className="text-end">{formatearMoneda(l.subtotal)}</td></tr>)}</tbody>
                <tfoot><tr><th colSpan={4}>Total</th><th className="text-end">{formatearMoneda(resultado.cotizacion.total)}</th></tr></tfoot>
              </table></div>
              <p className="small text-secondary">Valor estimado: el valor definitivo se calcula con los datos reales de los ocupantes.</p>
            </>}
            {resultado.disponible && (puedeReservar
              ? <Link className="btn btn-success w-100" to={`/reservar/${apartamento.id}?${consulta}`}>Reservar este apartamento</Link>
              : <Link className="btn btn-outline-primary w-100" to="/login">Inicia sesión como huésped para reservar</Link>)}
          </div>}
        </div></section>
      </div>
    </div>
  </main>
}
