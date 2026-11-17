import type { PerfilUsuario } from '../modelos/perfil'
import type { RolUsuario } from '../modelos/usuario'
import { api } from './api'

export interface UsuarioAdminRequest { nombre: string; apellido: string; documento: string; correo: string; telefono: string; fechaNacimiento: string; rol: RolUsuario; contrasena?: string }

export async function listarUsuarios(): Promise<PerfilUsuario[]> {
  return (await api.get<PerfilUsuario[]>('/usuarios')).data
}

export async function crearUsuarioAdministrativo(datos: UsuarioAdminRequest & { contrasena: string }): Promise<PerfilUsuario> {
  return (await api.post<PerfilUsuario>('/usuarios', datos)).data
}

export async function modificarUsuarioAdministrativo(id: string, datos: UsuarioAdminRequest): Promise<PerfilUsuario> {
  return (await api.put<PerfilUsuario>(`/usuarios/${id}`, { nombre: datos.nombre, apellido: datos.apellido, telefono: datos.telefono, rol: datos.rol })).data
}

export async function cambiarEstadoUsuario(id: string, activo: boolean): Promise<PerfilUsuario> {
  return (await api.patch<PerfilUsuario>(`/usuarios/${id}/estado`, { activo })).data
}

export async function obtenerUsuario(id: string): Promise<PerfilUsuario> {
  return (await api.get<PerfilUsuario>(`/usuarios/${id}`)).data
}
