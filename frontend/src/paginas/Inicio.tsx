import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { Paginacion } from '../componentes/Paginacion'
import { TarjetaApartamento } from '../componentes/TarjetaApartamento'
import { esquemaBusqueda } from '../esquemas/apartamento'
import type { FormularioBusqueda } from '../esquemas/apartamento'
import type { ApartamentoResponse } from '../modelos/apartamento'
import type { Pagina } from '../modelos/comun'
import { buscarDisponibles, buscarPorCapacidad, cotizar } from '../servicios/apartamentoServicio'
import { extraerMensajeError } from '../utilidades/errores'
import { fechasNacimientoGrupo, hoyISO, sumarDias } from '../utilidades/fechas'

const FILTROS_INICIALES: FormularioBusqueda = { fechaEntrada: '', fechaSalida: '', adultos: 1, menores: 0, precioMaximo: '' }

export default function Inicio() {
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormularioBusqueda>({
    resolver: zodResolver(esquemaBusqueda), defaultValues: FILTROS_INICIALES,
  })
  const [filtros, setFiltros] = useState<FormularioBusqueda>(FILTROS_INICIALES)
  const [pagina, setPagina] = useState(1)
  const [resultado, setResultado] = useState<Pagina<ApartamentoResponse> | null>(null)
  const [totales, setTotales] = useState<Record<string, number>>({})
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let activo = true
    async function cargar() {
      setCargando(true)
      setError('')
      try {
        const conFechas = Boolean(filtros.fechaEntrada && filtros.fechaSalida)
        const filtrarPrecio = filtros.precioMaximo.trim() !== ''
        const grupo = { fechaEntrada: filtros.fechaEntrada, fechaSalida: filtros.fechaSalida, fechasNacimiento: fechasNacimientoGrupo(filtros.adultos, filtros.menores) }
        let respuesta = conFechas
          ? await buscarDisponibles(grupo, filtrarPrecio ? 0 : pagina - 1)
          : await buscarPorCapacidad(filtros.adultos + filtros.menores, pagina - 1)
        if (conFechas && filtrarPrecio) {
          const contenido = [...respuesta.contenido]
          for (let indice = 1; indice < respuesta.totalPaginas; indice += 1) {
            const siguiente = await buscarDisponibles(grupo, indice)
            contenido.push(...siguiente.contenido)
          }
          respuesta = { ...respuesta, contenido }
        }
        if (!activo) return
        setResultado(respuesta)
        if (!conFechas) { setTotales({}); return }
        const cotizaciones = await Promise.allSettled(respuesta.contenido.map((a) => cotizar(a.id, grupo)))
        if (!activo) return
        const nuevos: Record<string, number> = {}
        cotizaciones.forEach((c, i) => { if (c.status === 'fulfilled') nuevos[respuesta.contenido[i].id] = c.value.total })
        setTotales(nuevos)
        if (filtrarPrecio && cotizaciones.some((c) => c.status === 'rejected')) {
          setError('No fue posible calcular el precio de algunos apartamentos; se omitieron del resultado filtrado.')
        }
      } catch (causa) {
        if (activo) setError(extraerMensajeError(causa))
      } finally {
        if (activo) setCargando(false)
      }
    }
    void cargar()
    return () => { activo = false }
  }, [filtros, pagina])

  const buscar = (datos: FormularioBusqueda) => { setPagina(1); setFiltros(datos) }
  const limpiar = () => { reset(FILTROS_INICIALES); setPagina(1); setFiltros(FILTROS_INICIALES) }

  const conFechas = Boolean(filtros.fechaEntrada && filtros.fechaSalida)
  const consulta = conFechas ? new URLSearchParams({
    entrada: filtros.fechaEntrada, salida: filtros.fechaSalida, adultos: String(filtros.adultos), menores: String(filtros.menores),
  }).toString() : ''
  const tope = filtros.precioMaximo.trim() === '' ? null : Number(filtros.precioMaximo)
  const catalogo = resultado?.contenido ?? []
  const filtrados = tope === null ? catalogo : catalogo.filter((a) => totales[a.id] !== undefined && totales[a.id] <= tope)
  const tamanioPagina = resultado?.tamanio ?? 10
  const totalPaginas = tope === null ? (resultado?.totalPaginas ?? 1) : Math.max(1, Math.ceil(filtrados.length / tamanioPagina))
  const visibles = tope === null ? filtrados : filtrados.slice((pagina - 1) * tamanioPagina, pagina * tamanioPagina)

  return <main className="container py-4">
    <section className="p-4 p-md-5 rounded-4 bg-light shadow-sm mb-4">
      <p className="text-primary fw-semibold mb-1">Alojamiento turístico · El Cam</p>
      <h1 className="display-6 fw-bold">Encuentra tu apartamento</h1>
      <p className="lead mb-4">Indica tus fechas y tu grupo para ver disponibilidad y el valor estimado de tu estancia.</p>
      <form onSubmit={handleSubmit(buscar)} noValidate>
        <div className="row g-3">
          <div className="col-6 col-lg-2">
            <label className="form-label" htmlFor="fechaEntrada">Entrada</label>
            <input id="fechaEntrada" type="date" min={hoyISO()} className={`form-control${errors.fechaEntrada ? ' is-invalid' : ''}`} {...register('fechaEntrada')} />
            {errors.fechaEntrada && <div className="invalid-feedback">{errors.fechaEntrada.message}</div>}
          </div>
          <div className="col-6 col-lg-2">
            <label className="form-label" htmlFor="fechaSalida">Salida</label>
            <input id="fechaSalida" type="date" min={sumarDias(hoyISO(), 1)} className={`form-control${errors.fechaSalida ? ' is-invalid' : ''}`} {...register('fechaSalida')} />
            {errors.fechaSalida && <div className="invalid-feedback">{errors.fechaSalida.message}</div>}
          </div>
          <div className="col-4 col-lg-2">
            <label className="form-label" htmlFor="adultos">Adultos</label>
            <input id="adultos" type="number" min={1} className={`form-control${errors.adultos ? ' is-invalid' : ''}`} {...register('adultos', { valueAsNumber: true })} />
            {errors.adultos && <div className="invalid-feedback">{errors.adultos.message}</div>}
          </div>
          <div className="col-4 col-lg-2">
            <label className="form-label" htmlFor="menores">Menores</label>
            <input id="menores" type="number" min={0} className={`form-control${errors.menores ? ' is-invalid' : ''}`} {...register('menores', { valueAsNumber: true })} />
            {errors.menores && <div className="invalid-feedback">{errors.menores.message}</div>}
          </div>
          <div className="col-4 col-lg-2">
            <label className="form-label" htmlFor="precioMaximo">Precio máx. (COP)</label>
            <input id="precioMaximo" type="number" min={0} className={`form-control${errors.precioMaximo ? ' is-invalid' : ''}`} {...register('precioMaximo')} />
            {errors.precioMaximo && <div className="invalid-feedback">{errors.precioMaximo.message}</div>}
          </div>
          <div className="col-12 col-lg-2 d-flex align-items-end gap-2">
            <button className="btn btn-primary flex-grow-1" type="submit">Buscar</button>
            <button className="btn btn-outline-secondary" type="button" onClick={limpiar}>Limpiar</button>
          </div>
        </div>
        <p className="form-text mt-2 mb-0">Sin fechas se muestra el catálogo según la capacidad necesaria. Con fechas se muestran solo los apartamentos disponibles.</p>
      </form>
    </section>

    <AlertaNotificacion mensaje={error} tipo="error" />
    {cargando ? <p className="text-secondary">Cargando apartamentos…</p> : <>
      {visibles.length === 0 ? <AlertaNotificacion mensaje="No encontramos apartamentos con esos criterios. Prueba con otras fechas o un grupo diferente." />
        : <div className="row g-4">{visibles.map((a) => <div className="col-12 col-md-6 col-lg-4" key={a.id}>
          <TarjetaApartamento apartamento={a} total={totales[a.id]} consulta={consulta} />
        </div>)}</div>}
      <div className="mt-4"><Paginacion pagina={pagina} totalPaginas={totalPaginas} alCambiar={setPagina} /></div>
    </>}
  </main>
}
