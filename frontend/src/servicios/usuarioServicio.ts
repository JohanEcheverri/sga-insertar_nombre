import type { PerfilUsuario } from '../modelos/perfil'
import { api } from './api'

export async function obtenerUsuario(id: string): Promise<PerfilUsuario> {
  return (await api.get<PerfilUsuario>(`/usuarios/${id}`)).data
}
