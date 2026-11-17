import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaNovedad } from '../../esquemas/admin'
import type { ApartamentoResponse } from '../../modelos/apartamento'
import type { NovedadResponse } from '../../modelos/admin'
import { GRAVEDADES } from '../../modelos/admin'
import { crearNovedad, eliminarNovedad, listarNovedades, listarPendientesPreparacion } from '../../servicios/operacionServicio'
import { formatearFechaHora } from '../../utilidades/fechas'
import { etiqueta } from '../../utilidades/formato'
import { validar } from '../../utilidades/validacion'
import { InsigniaEstado } from '../InsigniaEstado'

interface Props {
  apartamentos: ApartamentoResponse[]
  autorPorDefecto: string
  avisos: Avisos
  soloLectura?: boolean
}

const COLOR_GRAVEDAD: Record<string, string> = { BAJA: 'secondary', MEDIA: 'info', ALTA: 'warning', CRITICA: 'danger' }

/** Novedades por apartamento y unidades pendientes de preparación. Reportar no cambia el estado operativo (RN-31). */
export function PanelNovedades({ apartamentos, autorPorDefecto, avisos, soloLectura = false }: Props) {
  const [apartamentoId, setApartamentoId] = useState('')
  const [novedades, setNovedades] = useState<NovedadResponse[]>([])
  const [pendientes, setPendientes] = useState<ApartamentoResponse[]>([])
  const [form, setForm] = useState({ autor: autorPorDefecto, descripcion: '', gravedad: 'MEDIA' })
  const [errores, setErrores] = useState<Record<string, string>>({})
  const { exito, fallo } = avisos

  const cargarNovedades = useCallback(async (id: string) => {
    if (!id) { setNovedades([]); return }
    try { setNovedades(await listarNovedades(id)) } catch (causa) { fallo(causa) }
  }, [fallo])

  const cargarPendientes = useCallback(async () => {
    try { setPendientes((await listarPendientesPreparacion()).contenido) } catch (causa) { fallo(causa) }
  }, [fallo])

  useEffect(() => { void cargarNovedades(apartamentoId) }, [apartamentoId, cargarNovedades])
  useEffect(() => { void cargarPendientes() }, [cargarPendientes])

  const enviar = async (e: FormEvent) => {
    e.preventDefault()
    if (!apartamentoId) { setErrores({ apartamento: 'Selecciona un apartamento.' }); return }
    const { datos, errores: errs } = validar(esquemaNovedad, form)
    setErrores(errs)
    if (!datos) return
    try {
      await crearNovedad(apartamentoId, datos)
      setForm({ ...form, descripcion: '' })
      exito('Novedad registrada.')
      await cargarNovedades(apartamentoId)
    } catch (causa) { fallo(causa) }
  }

  const anular = async (id: string) => {
    if (!window.confirm('¿Anular esta novedad?')) return
    try { await eliminarNovedad(id); exito('Novedad anulada.'); await cargarNovedades(apartamentoId) } catch (causa) { fallo(causa) }
  }

  return <section className="row g-4">
    <div className="col-12 col-lg-7">
      <h2 className="h5">Novedades de un apartamento</h2>
      <select className="form-select mb-3" aria-label="Apartamento" value={apartamentoId} onChange={(e) => setApartamentoId(e.target.value)}>
        <option value="">Selecciona un apartamento…</option>
        {apartamentos.map((a) => <option key={a.id} value={a.id}>{a.identificadorUnico} — {a.descripcion}</option>)}
      </select>
      {apartamentoId && (novedades.length === 0 ? <p className="text-secondary">Sin novedades registradas.</p>
        : <ul className="list-group">{novedades.map((n) => <li className="list-group-item" key={n.id}>
          <div className="d-flex justify-content-between align-items-start gap-2">
            <div><span className={`badge text-bg-${COLOR_GRAVEDAD[n.gravedad]} me-2`}>{etiqueta(n.gravedad)}</span>{n.descripcion}
              <div className="small text-secondary">{n.autor} · {formatearFechaHora(n.fechaReporte)}</div></div>
            {!soloLectura && <button className="btn btn-outline-danger btn-sm" type="button" onClick={() => void anular(n.id)}>Anular</button>}
          </div></li>)}</ul>)}
    </div>

    <div className="col-12 col-lg-5">
      {!soloLectura && <form onSubmit={enviar} noValidate className="mb-4"><h2 className="h5">Registrar novedad</h2>
        {errores.apartamento && <div className="text-danger small">{errores.apartamento}</div>}
        <input className="form-control my-1" placeholder="Autor" aria-label="Autor" value={form.autor} onChange={(e) => setForm({ ...form, autor: e.target.value })} />
        {errores.autor && <div className="text-danger small">{errores.autor}</div>}
        <textarea className="form-control my-1" rows={3} placeholder="Describe el daño, faltante o situación" aria-label="Descripción" value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} />
        {errores.descripcion && <div className="text-danger small">{errores.descripcion}</div>}
        <select className="form-select my-1" aria-label="Gravedad" value={form.gravedad} onChange={(e) => setForm({ ...form, gravedad: e.target.value })}>{GRAVEDADES.map((g) => <option key={g} value={g}>{etiqueta(g)}</option>)}</select>
        <button className="btn btn-primary w-100 mt-1" type="submit">Registrar novedad</button>
      </form>}

      <div className="d-flex justify-content-between align-items-center"><h2 className="h5 mb-0">Pendientes de preparación</h2>
        <button className="btn btn-outline-secondary btn-sm" type="button" onClick={() => void cargarPendientes()}>Actualizar</button></div>
      {pendientes.length === 0 ? <p className="text-secondary mt-2">No hay apartamentos pendientes.</p>
        : <ul className="list-group mt-2">{pendientes.map((a) => <li className="list-group-item d-flex justify-content-between" key={a.id}><span>Apartamento {a.identificadorUnico}</span><InsigniaEstado estado={a.estadoOperativo} /></li>)}</ul>}
    </div>
  </section>
}
