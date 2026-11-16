import type { LoginRequest, RegistroRequest, TokenResponse } from '../modelos/usuario'
import { api } from './api'

export async function iniciarSesion(datos: LoginRequest): Promise<TokenResponse> {
  const respuesta = await api.post<TokenResponse>('/auth/login', datos)
  return respuesta.data
}

export async function registrarHuesped(datos: RegistroRequest): Promise<void> {
  await api.post<TokenResponse>('/auth/registro', datos)
}
