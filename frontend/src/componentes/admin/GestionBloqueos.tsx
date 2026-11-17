import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaBloqueo } from '../../esquemas/admin'
import type { ApartamentoResponse } from '../../modelos/apartamento'
import type { BloqueoResponse } from '../../modelos/admin'
import { crearBloqueo, listarBloqueos } from '../../servicios/adminServicio'
import { formatearFecha, hoyISO } from '../../utilidades/fechas'
import { validar } from '../../utilidades/validacion'

interface Props {
  apartamentos: ApartamentoResponse[]
  avisos: Avisos
}

/** Bloqueos administrativos: no se pueden crear sobre noches con reservas activas (RN-28) ni cambian el estado operativo (RN-24). */
export function GestionBloqueos({ apartamentos, avisos }: Props) {
  const [apartamentoId, setApartamentoId] = useState('')
  const [bloqueos, setBloqueos] = useState<BloqueoResponse[]>([])
  const [form, setForm] = useState({ fechaInicio: '', fechaFin: '', motivo: '' })
  const [errores, setErrores] = useState<Record<string, string>>({})
  const { exito, fallo } = avisos

  const cargar = useCallback(async (id: string) => {
    if (!id) { setBloqueos([]); return }
    try { setBloqueos(await listarBloqueos(id)) } catch (causa) { fallo(causa) }
  }, [fallo])

  useEffect(() => { void cargar(apartamentoId) }, [apartamentoId, cargar])

  const enviar = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores: errs } = validar(esquemaBloqueo, form)
    if (!apartamentoId) errs.apartamento = 'Selecciona un apartamento.'
    setErrores(errs)
    if (!datos || !apartamentoId) return
    try {
      await crearBloqueo({ apartamentoId, ...datos })
      exito('Bloqueo registrado.')
      setForm({ fechaInicio: '', fechaFin: '', motivo: '' })
      await cargar(apartamentoId)
    } catch (causa) { fallo(causa) }
  }

  return <section className="row g-4">
    <div className="col-12 col-lg-7">
      <h2 className="h5">Bloqueos del apartamento</h2>
      <select className="form-select mb-3" aria-label="Apartamento" value={apartamentoId} onChange={(e) => setApartamentoId(e.target.value)}>
        <option value="">Selecciona un apartamento…</option>{apartamentos.map((a) => <option key={a.id} value={a.id}>{a.identificadorUnico}</option>)}</select>
      {apartamentoId && (bloqueos.length === 0 ? <p className="text-secondary">Sin bloqueos registrados.</p>
        : <table className="table table-sm"><thead><tr><th>Desde</th><th>Hasta (exclusiva)</th><th>Motivo</th><th>Vigente</th></tr></thead>
          <tbody>{bloqueos.map((b) => <tr key={b.id}><td>{formatearFecha(b.fechaInicio)}</td><td>{formatearFecha(b.fechaFin)}</td><td>{b.motivo}</td>
            <td><span className={`badge text-bg-${b.activo ? 'success' : 'secondary'}`}>{b.activo ? 'Sí' : 'No'}</span></td></tr>)}</tbody></table>)}
    </div>
    <div className="col-12 col-lg-5">
      <form onSubmit={enviar} noValidate className="card card-body shadow-sm"><h2 className="h5">Nuevo bloqueo</h2>
        {errores.apartamento && <div className="text-danger small">{errores.apartamento}</div>}
        <label className="form-label small" htmlFor="bInicio">Desde</label>
        <input id="bInicio" type="date" min={hoyISO()} className={`form-control${errores.fechaInicio ? ' is-invalid' : ''}`} value={form.fechaInicio} onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })} />
        {errores.fechaInicio && <div className="invalid-feedback">{errores.fechaInicio}</div>}
        <label className="form-label small mt-2" htmlFor="bFin">Hasta (la noche de esta fecha no se bloquea)</label>
        <input id="bFin" type="date" className={`form-control${errores.fechaFin ? ' is-invalid' : ''}`} value={form.fechaFin} onChange={(e) => setForm({ ...form, fechaFin: e.target.value })} />
        {errores.fechaFin && <div className="invalid-feedback">{errores.fechaFin}</div>}
        <label className="form-label small mt-2" htmlFor="bMotivo">Motivo</label>
        <input id="bMotivo" className={`form-control${errores.motivo ? ' is-invalid' : ''}`} value={form.motivo} onChange={(e) => setForm({ ...form, motivo: e.target.value })} />
        {errores.motivo && <div className="invalid-feedback">{errores.motivo}</div>}
        <button className="btn btn-primary mt-3" type="submit">Registrar bloqueo</button>
      </form>
    </div>
  </section>
}
