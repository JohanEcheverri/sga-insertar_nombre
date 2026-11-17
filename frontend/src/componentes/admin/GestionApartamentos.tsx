import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaApartamento } from '../../esquemas/apartamento'
import type { ApartamentoResponse, CategoriaApartamento, EstadoOperativo } from '../../modelos/apartamento'
import type { TemporadaResponse } from '../../modelos/admin'
import { cambiarEstadoOperativo, crearApartamento, modificarApartamento, retirarApartamento } from '../../servicios/adminServicio'
import { etiqueta } from '../../utilidades/formato'
import { validar } from '../../utilidades/validacion'
import { InsigniaEstado } from '../InsigniaEstado'

interface Props {
  apartamentos: ApartamentoResponse[]
  temporadas: TemporadaResponse[]
  avisos: Avisos
  alCambiar: () => void
}

interface DormitorioForm { nombre: string; cantidadCamas: string; tipoCamas: string }
interface FormApto { identificadorUnico: string; descripcion: string; categoria: CategoriaApartamento; aforoMaximo: string; dormitorios: DormitorioForm[]; tarifas: Record<string, string> }

const DORMITORIO_VACIO: DormitorioForm = { nombre: '', cantidadCamas: '1', tipoCamas: 'Doble' }
const cantidadDormitorios = (categoria: CategoriaApartamento) => categoria === 'PREMIUM' ? 3 : 2
const dormitoriosNuevos = (categoria: CategoriaApartamento) => Array.from(
  { length: cantidadDormitorios(categoria) }, (_, i) => ({ ...DORMITORIO_VACIO, nombre: `Dormitorio ${i + 1}` }),
)
const FORM_VACIO: FormApto = { identificadorUnico: '', descripcion: '', categoria: 'STANDARD', aforoMaximo: '3', dormitorios: dormitoriosNuevos('STANDARD'), tarifas: {} }
const ESTADOS_MANUALES: EstadoOperativo[] = ['PENDIENTE_PREPARACION', 'EN_PREPARACION', 'PREPARADO', 'FUERA_DE_SERVICIO']

/** El backend entrega los dormitorios como "Nombre: N cama(s) tipo"; se reconstruyen para editar. */
function parsearDormitorio(texto: string): DormitorioForm {
  const m = /^(.*): (\d+) cama\(s\) (.*)$/.exec(texto)
  return m ? { nombre: m[1], cantidadCamas: m[2], tipoCamas: m[3] } : { nombre: texto, cantidadCamas: '1', tipoCamas: 'Doble' }
}

function FilaApartamento({ apto, alEditar, alRetirar, avisos, alCambiar }: { apto: ApartamentoResponse; alEditar: () => void; alRetirar: () => void; avisos: Avisos; alCambiar: () => void }) {
  const [estado, setEstado] = useState<EstadoOperativo>(apto.estadoOperativo)
  useEffect(() => setEstado(apto.estadoOperativo), [apto.estadoOperativo])
  const aplicar = async () => {
    try { await cambiarEstadoOperativo(apto.id, estado); avisos.exito(`Estado de ${apto.identificadorUnico} actualizado.`); alCambiar() } catch (causa) { avisos.fallo(causa) }
  }
  return <tr>
    <td><strong>Apartamento {apto.identificadorUnico}</strong><div className="small text-secondary">{apto.categoria === 'PREMIUM' ? 'Premium' : 'Standard'} · {apto.descripcion}</div></td>
    <td>{apto.aforoMaximo}</td>
    <td className="small">{apto.dormitorios.map((d) => <div key={d}>{d}</div>)}</td>
    <td><InsigniaEstado estado={apto.estadoOperativo} />
      <div className="input-group input-group-sm mt-1" style={{ minWidth: 220 }}>
        <select className="form-select" aria-label="Nuevo estado" value={estado} onChange={(e) => setEstado(e.target.value as EstadoOperativo)}>{ESTADOS_MANUALES.map((s) => <option key={s} value={s}>{etiqueta(s)}</option>)}</select>
        <button className="btn btn-outline-secondary" type="button" onClick={() => void aplicar()}>Aplicar</button></div></td>
    <td><div className="d-flex gap-1"><button className="btn btn-outline-primary btn-sm" type="button" onClick={alEditar}>Editar</button>
      <button className="btn btn-outline-danger btn-sm" type="button" onClick={alRetirar}>Retirar</button></div></td>
  </tr>
}

export function GestionApartamentos({ apartamentos, temporadas, avisos, alCambiar }: Props) {
  const [editando, setEditando] = useState<ApartamentoResponse | null>(null)
  const [form, setForm] = useState<FormApto>(FORM_VACIO)
  const [errores, setErrores] = useState<Record<string, string>>({})
  const [guardando, setGuardando] = useState(false)

  const iniciarEdicion = (a: ApartamentoResponse) => {
    setEditando(a)
    setErrores({})
    setForm({ identificadorUnico: a.identificadorUnico, descripcion: a.descripcion, categoria: a.categoria, aforoMaximo: String(a.aforoMaximo), dormitorios: a.dormitorios.map(parsearDormitorio), tarifas: {} })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
  const cancelarEdicion = () => { setEditando(null); setErrores({}); setForm(FORM_VACIO) }
  const cambiarDorm = (i: number, clave: keyof DormitorioForm, valor: string) =>
    setForm({ ...form, dormitorios: form.dormitorios.map((d, j) => (j === i ? { ...d, [clave]: valor } : d)) })
  const cambiarCategoria = (categoria: CategoriaApartamento) => setForm({
    ...form,
    categoria,
    aforoMaximo: String(categoria === 'PREMIUM' ? 6 : 3),
    dormitorios: dormitoriosNuevos(categoria),
  })

  const enviar = async (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores: errs } = validar(esquemaApartamento, form)
    if (!editando) {
      for (const t of temporadas) if (!(Number(form.tarifas[t.id]) > 0)) errs[`tarifa.${t.id}`] = 'Indica una tarifa mayor que cero.'
    }
    setErrores(errs)
    if (!datos || Object.keys(errs).length > 0) return
    setGuardando(true)
    try {
      if (editando) {
        await modificarApartamento(editando.id, { descripcion: datos.descripcion, aforoMaximo: datos.aforoMaximo, dormitorios: datos.dormitorios })
        avisos.exito('Apartamento actualizado. Las reservas existentes no se modifican aunque superen la nueva capacidad.')
      } else {
        await crearApartamento({ ...datos, tarifas: temporadas.map((t) => ({ temporadaId: t.id, precioPorNochePorOcupante: Number(form.tarifas[t.id]) })) })
        avisos.exito('Apartamento creado con sus tarifas.')
      }
      cancelarEdicion()
      alCambiar()
    } catch (causa) { avisos.fallo(causa) } finally { setGuardando(false) }
  }

  const retirar = async (a: ApartamentoResponse) => {
    if (!window.confirm(`¿Retirar el apartamento ${a.identificadorUnico}? Es una eliminación lógica y solo procede sin reservas activas ni futuras.`)) return
    try { await retirarApartamento(a.id); avisos.exito('Apartamento retirado.'); alCambiar() } catch (causa) { avisos.fallo(causa) }
  }

  return <section>
    <form className="card shadow-sm mb-4" onSubmit={enviar} noValidate><div className="card-body">
      <h2 className="h5">{editando ? `Editar apartamento ${editando.identificadorUnico}` : 'Nuevo apartamento'}</h2>
      <div className="row g-2">
        <div className="col-12 col-md-3"><label className="form-label small" htmlFor="idApto">Identificador único</label>
          <input id="idApto" className={`form-control${errores.identificadorUnico ? ' is-invalid' : ''}`} readOnly={Boolean(editando)} value={form.identificadorUnico} onChange={(e) => setForm({ ...form, identificadorUnico: e.target.value })} />
          {errores.identificadorUnico && <div className="invalid-feedback">{errores.identificadorUnico}</div>}</div>
        <div className="col-12 col-md-3"><label className="form-label small" htmlFor="categoriaApto">Categoría</label>
          <select id="categoriaApto" className="form-select" value={form.categoria} disabled={Boolean(editando)} onChange={(e) => cambiarCategoria(e.target.value as CategoriaApartamento)}>
            <option value="STANDARD">Standard</option><option value="PREMIUM">Premium</option>
          </select></div>
        <div className="col-12 col-md-4"><label className="form-label small" htmlFor="descApto">Descripción</label>
          <input id="descApto" className={`form-control${errores.descripcion ? ' is-invalid' : ''}`} value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} />
          {errores.descripcion && <div className="invalid-feedback">{errores.descripcion}</div>}</div>
        <div className="col-12 col-md-2"><label className="form-label small" htmlFor="aforoApto">Aforo</label>
          <input id="aforoApto" type="number" className={`form-control${errores.aforoMaximo ? ' is-invalid' : ''}`} readOnly value={form.aforoMaximo} />
          {errores.aforoMaximo && <div className="invalid-feedback">{errores.aforoMaximo}</div>}</div>
      </div>

      <div className="d-flex justify-content-between align-items-center mt-3"><h3 className="h6 mb-0">Dormitorios</h3>
        <span className="small text-secondary">{cantidadDormitorios(form.categoria)} para esta categoría</span></div>
      {errores.dormitorios && <div className="text-danger small">{errores.dormitorios}</div>}
      {form.dormitorios.map((d, i) => <div className="row g-2 mt-1" key={i}>
        <div className="col-12 col-md-4"><input className={`form-control${errores[`dormitorios.${i}.nombre`] ? ' is-invalid' : ''}`} placeholder="Nombre" aria-label={`Nombre dormitorio ${i + 1}`} value={d.nombre} onChange={(e) => cambiarDorm(i, 'nombre', e.target.value)} /><div className="invalid-feedback">{errores[`dormitorios.${i}.nombre`]}</div></div>
        <div className="col-5 col-md-3"><input type="number" min={1} className={`form-control${errores[`dormitorios.${i}.cantidadCamas`] ? ' is-invalid' : ''}`} aria-label={`Camas dormitorio ${i + 1}`} value={d.cantidadCamas} onChange={(e) => cambiarDorm(i, 'cantidadCamas', e.target.value)} /><div className="invalid-feedback">{errores[`dormitorios.${i}.cantidadCamas`]}</div></div>
        <div className="col-5 col-md-4"><input className={`form-control${errores[`dormitorios.${i}.tipoCamas`] ? ' is-invalid' : ''}`} placeholder="Tipo de camas" aria-label={`Tipo de camas ${i + 1}`} value={d.tipoCamas} onChange={(e) => cambiarDorm(i, 'tipoCamas', e.target.value)} /><div className="invalid-feedback">{errores[`dormitorios.${i}.tipoCamas`]}</div></div>
        <div className="col-2 col-md-1"><button type="button" className="btn btn-outline-danger w-100" disabled aria-label="La cantidad de dormitorios es fija por categoría">×</button></div>
      </div>)}

      {!editando && <>
        <h3 className="h6 mt-3">Tarifa por {form.categoria === 'PREMIUM' ? 'persona facturable' : 'apartamento'} y noche, en cada temporada</h3>
        <div className="row g-2">{temporadas.map((t) => <div className="col-12 col-md-4" key={t.id}>
          <label className="form-label small" htmlFor={`t${t.id}`}>{t.nombre}</label>
          <input id={`t${t.id}`} type="number" min={0} className={`form-control${errores[`tarifa.${t.id}`] ? ' is-invalid' : ''}`} value={form.tarifas[t.id] ?? ''} onChange={(e) => setForm({ ...form, tarifas: { ...form.tarifas, [t.id]: e.target.value } })} />
          {errores[`tarifa.${t.id}`] && <div className="invalid-feedback">{errores[`tarifa.${t.id}`]}</div>}</div>)}</div>
      </>}

      <div className="mt-3 d-flex gap-2">
        <button className="btn btn-primary" type="submit" disabled={guardando}>{guardando ? 'Guardando…' : editando ? 'Guardar cambios' : 'Crear apartamento'}</button>
        {editando && <button className="btn btn-outline-secondary" type="button" onClick={cancelarEdicion}>Cancelar</button>}
      </div>
    </div></form>

    <div className="table-responsive"><table className="table align-middle">
      <thead><tr><th>Apartamento</th><th>Cap.</th><th>Dormitorios</th><th>Estado operativo</th><th>Acciones</th></tr></thead>
      <tbody>{apartamentos.map((a) => <FilaApartamento key={a.id} apto={a} alEditar={() => iniciarEdicion(a)} alRetirar={() => void retirar(a)} avisos={avisos} alCambiar={alCambiar} />)}</tbody>
    </table></div>
  </section>
}
