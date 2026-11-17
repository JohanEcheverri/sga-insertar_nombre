import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import type { PerfilUsuario } from '../../modelos/perfil'
import type { RolUsuario } from '../../modelos/usuario'
import { cambiarEstadoUsuario, crearUsuarioAdministrativo, listarUsuarios, modificarUsuarioAdministrativo } from '../../servicios/usuarioServicio'

const ROLES: { valor: RolUsuario; texto: string }[] = [
  { valor: 'ADMINISTRADOR', texto: 'Administrador' }, { valor: 'RECEPCIONISTA', texto: 'Recepcionista' },
  { valor: 'PERSONAL_SERVICIO', texto: 'Personal de Servicio' }, { valor: 'HUESPED', texto: 'Huésped' },
]
type Datos = { nombre: string; apellido: string; documento: string; correo: string; telefono: string; fechaNacimiento: string; rol: RolUsuario; contrasena: string }
const VACIO: Datos = { nombre: '', apellido: '', documento: '', correo: '', telefono: '', fechaNacimiento: '', rol: 'HUESPED', contrasena: '' }

export function GestionUsuarios({ avisos }: { avisos: Avisos }) {
  const [usuarios, setUsuarios] = useState<PerfilUsuario[]>([])
  const [datos, setDatos] = useState<Datos>(VACIO)
  const [editando, setEditando] = useState<string | null>(null)
  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')
  const cargar = useCallback(async () => { try { setUsuarios(await listarUsuarios()) } catch (e) { avisos.fallo(e) } finally { setCargando(false) } }, [avisos])
  useEffect(() => { void cargar() }, [cargar])
  const iniciarEdicion = (u: PerfilUsuario) => { setEditando(u.id); setError(''); setDatos({ nombre: u.nombre, apellido: u.apellido, documento: u.documento, correo: u.correo, telefono: u.telefono, fechaNacimiento: u.fechaNacimiento, rol: u.rol as RolUsuario, contrasena: '' }) }
  const cancelarEdicion = () => { setEditando(null); setDatos(VACIO); setError('') }
  const enviar = async (e: FormEvent) => {
    e.preventDefault(); setError('')
    if (!datos.nombre.trim() || !datos.apellido.trim() || !datos.telefono.trim() || (!editando && (!datos.documento.trim() || !datos.correo.trim() || !datos.fechaNacimiento || datos.contrasena.length < 8))) { setError('Completa los campos obligatorios. La contraseña debe tener al menos 8 caracteres.'); return }
    setGuardando(true)
    try {
      if (editando) await modificarUsuarioAdministrativo(editando, datos)
      else await crearUsuarioAdministrativo(datos)
      avisos.exito(editando ? 'Usuario actualizado.' : 'Usuario creado.')
      cancelarEdicion(); await cargar()
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudo guardar el usuario.') } finally { setGuardando(false) }
  }
  const alternar = async (u: PerfilUsuario) => { try { await cambiarEstadoUsuario(u.id, !u.activo); avisos.exito(`Cuenta ${u.activo ? 'desactivada' : 'activada'}.`); await cargar() } catch (e) { avisos.fallo(e) } }
  return <section>
    <h2 className="h5">Usuarios del sistema</h2>
    <form className="card card-body mb-4" onSubmit={(e) => void enviar(e)}>
      <h3 className="h6">{editando ? 'Editar usuario' : 'Crear usuario'}</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="row g-2">
        {([['nombre','Nombre'],['apellido','Apellido'],['documento','Documento'],['correo','Correo'],['telefono','Teléfono']] as const).map(([k,label]) => <div className="col-12 col-md-4" key={k}><label className="form-label">{label}</label><input required className="form-control" type={k === 'correo' ? 'email' : 'text'} value={datos[k]} onChange={(e) => setDatos({ ...datos, [k]: e.target.value })} disabled={Boolean(editando && (k === 'documento' || k === 'correo'))} /></div>)}
        {!editando && <><div className="col-12 col-md-4"><label className="form-label">Fecha de nacimiento</label><input required className="form-control" type="date" max={new Date().toISOString().slice(0,10)} value={datos.fechaNacimiento} onChange={(e) => setDatos({ ...datos, fechaNacimiento: e.target.value })} /></div><div className="col-12 col-md-4"><label className="form-label">Contraseña inicial</label><input required minLength={8} className="form-control" type="password" value={datos.contrasena} onChange={(e) => setDatos({ ...datos, contrasena: e.target.value })} /></div></>}
        <div className="col-12 col-md-4"><label className="form-label">Rol</label><select className="form-select" value={datos.rol} onChange={(e) => setDatos({ ...datos, rol: e.target.value as RolUsuario })}>{ROLES.map((r) => <option key={r.valor} value={r.valor}>{r.texto}</option>)}</select></div>
      </div>
      <div className="mt-3 d-flex gap-2"><button className="btn btn-primary" disabled={guardando}>{guardando ? 'Guardando…' : editando ? 'Guardar cambios' : 'Crear usuario'}</button>{editando && <button type="button" className="btn btn-outline-secondary" onClick={cancelarEdicion}>Cancelar</button>}</div>
    </form>
    {cargando ? <p>Cargando usuarios…</p> : <div className="table-responsive"><table className="table align-middle"><thead><tr><th>Usuario</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>{usuarios.map((u) => <tr key={u.id}><td>{u.nombre} {u.apellido}<div className="small text-secondary">{u.documento}</div></td><td>{u.correo}</td><td>{ROLES.find((r) => r.valor === u.rol)?.texto ?? u.rol}</td><td>{u.activo ? 'Activo' : 'Inactivo'}</td><td><div className="d-flex gap-2"><button className="btn btn-sm btn-outline-primary" onClick={() => iniciarEdicion(u)}>Editar</button><button className={`btn btn-sm ${u.activo ? 'btn-outline-danger' : 'btn-outline-success'}`} onClick={() => void alternar(u)}>{u.activo ? 'Desactivar' : 'Activar'}</button></div></td></tr>)}</tbody></table></div>}
  </section>
}
