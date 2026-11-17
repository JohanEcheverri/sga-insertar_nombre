import { useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaTarifa, esquemaTemporada } from '../../esquemas/admin'
import type { ApartamentoResponse } from '../../modelos/apartamento'
import type { TemporadaResponse } from '../../modelos/admin'
import { asignarTarifa, crearTemporada, modificarTemporada, retirarTemporada } from '../../servicios/adminServicio'
import { formatearFecha } from '../../utilidades/fechas'
import { formatearMoneda } from '../../utilidades/formato'
import { validar } from '../../utilidades/validacion'

interface Props {
  apartamentos: ApartamentoResponse[]
  temporadas: TemporadaResponse[]
  avisos: Avisos
  alCambiar: () => void
}

type Errores = Record<string, string>

export function GestionTarifas({ apartamentos, temporadas, avisos, alCambiar }: Props) {
  const [tarifa, setTarifa] = useState({ temporadaId: '', apartamentoId: '', precio: '' })
  const [temporada, setTemporada] = useState({ nombre: '', fechaInicio: '', fechaFin: '' })
  const [editandoTemporada, setEditandoTemporada] = useState<TemporadaResponse | null>(null)
  const [errTarifa, setErrTarifa] = useState<Errores>({})
  const [errTemporada, setErrTemporada] = useState<Errores>({})

  const enviarTarifa = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores } = validar(esquemaTarifa, tarifa)
    setErrTarifa(errores)
    if (!datos) return
    try {
      await asignarTarifa({ temporadaId: datos.temporadaId, apartamentoId: datos.apartamentoId, precioPorNochePorOcupante: datos.precio })
      avisos.exito(`Tarifa de ${formatearMoneda(datos.precio)} asignada. Las reservas ya creadas conservan su valor congelado.`)
      setTarifa({ ...tarifa, precio: '' })
      alCambiar()
    } catch (causa) { avisos.fallo(causa) }
  }

  const enviarTemporada = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores } = validar(esquemaTemporada, temporada)
    setErrTemporada(errores)
    if (!datos) return
    try {
      if (editandoTemporada) {
        await modificarTemporada(editandoTemporada.id, datos)
        avisos.exito('Temporada actualizada.')
      } else {
        await crearTemporada(datos)
        avisos.exito('Temporada creada con las tarifas de la temporada base; ajústalas por apartamento.')
      }
      setEditandoTemporada(null)
      setTemporada({ nombre: '', fechaInicio: '', fechaFin: '' })
      alCambiar()
    } catch (causa) { avisos.fallo(causa) }
  }

  const retirar = async (t: TemporadaResponse) => {
    if (!window.confirm(`¿Retirar la temporada "${t.nombre}"? Sus fechas pasarán a la temporada base.`)) return
    try { await retirarTemporada(t.id); avisos.exito('Temporada retirada.'); alCambiar() } catch (causa) { avisos.fallo(causa) }
  }

  const editar = (t: TemporadaResponse) => {
    if (t.base || !t.fechaInicio || !t.fechaFin) return
    setEditandoTemporada(t)
    setTemporada({ nombre: t.nombre, fechaInicio: t.fechaInicio, fechaFin: t.fechaFin })
    setErrTemporada({})
  }

  const cancelarEdicion = () => {
    setEditandoTemporada(null)
    setTemporada({ nombre: '', fechaInicio: '', fechaFin: '' })
    setErrTemporada({})
  }

  const campoError = (errs: Errores, clave: string) => (errs[clave] ? <div className="invalid-feedback">{errs[clave]}</div> : null)

  return <section className="row g-4">
    <div className="col-12 col-lg-7">
      <h2 className="h5">Temporadas</h2>
      <div className="table-responsive"><table className="table table-sm align-middle">
        <thead><tr><th>Nombre</th><th>Desde</th><th>Hasta (inclusive)</th><th>Tarifas</th><th /></tr></thead>
        <tbody>{temporadas.map((t) => <tr key={t.id}>
          <td>{t.nombre} {t.base && <span className="badge text-bg-secondary">Base</span>}</td>
          <td>{t.base ? 'Resto del año' : formatearFecha(t.fechaInicio)}</td><td>{t.base ? '—' : formatearFecha(t.fechaFin)}</td><td>{t.cantidadTarifas}</td>
          <td>{!t.base && <div className="d-flex gap-1"><button className="btn btn-outline-primary btn-sm" type="button" onClick={() => editar(t)}>Editar</button>
            <button className="btn btn-outline-danger btn-sm" type="button" onClick={() => void retirar(t)}>Retirar</button></div>}</td></tr>)}</tbody>
      </table></div>

      <form onSubmit={enviarTemporada} noValidate className="card card-body shadow-sm mt-3"><h3 className="h6">{editandoTemporada ? `Editar temporada: ${editandoTemporada.nombre}` : 'Nueva temporada'}</h3>
        <div className="row g-2">
          <div className="col-12 col-md-4"><input className={`form-control${errTemporada.nombre ? ' is-invalid' : ''}`} placeholder="Nombre" aria-label="Nombre de la temporada" value={temporada.nombre} onChange={(e) => setTemporada({ ...temporada, nombre: e.target.value })} />{campoError(errTemporada, 'nombre')}</div>
          <div className="col-6 col-md-3"><input type="date" className={`form-control${errTemporada.fechaInicio ? ' is-invalid' : ''}`} aria-label="Fecha de inicio" value={temporada.fechaInicio} onChange={(e) => setTemporada({ ...temporada, fechaInicio: e.target.value })} />{campoError(errTemporada, 'fechaInicio')}</div>
          <div className="col-6 col-md-3"><input type="date" className={`form-control${errTemporada.fechaFin ? ' is-invalid' : ''}`} aria-label="Fecha de fin" value={temporada.fechaFin} onChange={(e) => setTemporada({ ...temporada, fechaFin: e.target.value })} />{campoError(errTemporada, 'fechaFin')}</div>
          <div className="col-12 col-md-2 d-flex gap-2"><button className="btn btn-primary w-100" type="submit">{editandoTemporada ? 'Guardar' : 'Crear'}</button>
            {editandoTemporada && <button className="btn btn-outline-secondary" type="button" onClick={cancelarEdicion}>Cancelar</button>}</div>
        </div>
        <p className="form-text mb-0">Las temporadas no pueden solaparse. El rango es inclusivo (del 10 al 20 incluye ambos días).</p>
      </form>
    </div>

    <div className="col-12 col-lg-5">
      <form onSubmit={enviarTarifa} noValidate className="card card-body shadow-sm"><h2 className="h5">Asignar tarifa</h2>
        <label className="form-label small" htmlFor="tTemporada">Temporada</label>
        <select id="tTemporada" className={`form-select${errTarifa.temporadaId ? ' is-invalid' : ''}`} value={tarifa.temporadaId} onChange={(e) => setTarifa({ ...tarifa, temporadaId: e.target.value })}>
          <option value="">Selecciona…</option>{temporadas.map((t) => <option key={t.id} value={t.id}>{t.nombre}</option>)}</select>{campoError(errTarifa, 'temporadaId')}
        <label className="form-label small mt-2" htmlFor="tApto">Apartamento</label>
        <select id="tApto" className={`form-select${errTarifa.apartamentoId ? ' is-invalid' : ''}`} value={tarifa.apartamentoId} onChange={(e) => setTarifa({ ...tarifa, apartamentoId: e.target.value })}>
          <option value="">Selecciona…</option>{apartamentos.map((a) => <option key={a.id} value={a.id}>{a.identificadorUnico}</option>)}</select>{campoError(errTarifa, 'apartamentoId')}
        <label className="form-label small mt-2" htmlFor="tPrecio">Valor por ocupante facturable y noche (COP)</label>
        <input id="tPrecio" type="number" min={0} className={`form-control${errTarifa.precio ? ' is-invalid' : ''}`} value={tarifa.precio} onChange={(e) => setTarifa({ ...tarifa, precio: e.target.value })} />{campoError(errTarifa, 'precio')}
        <button className="btn btn-primary mt-3" type="submit">Asignar tarifa</button>
      </form>
    </div>
  </section>
}
