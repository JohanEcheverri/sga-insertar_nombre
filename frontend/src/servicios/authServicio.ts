import type { LoginRequest, RegistroRequest, TokenResponse } from '../modelos/auth'
import { api } from './api'
export async function iniciarSesion(datos: LoginRequest): Promise<TokenResponse> { return (await api.post<TokenResponse>('/auth/login', datos)).data }
export async function registrarUsuario(datos: RegistroRequest): Promise<TokenResponse> { return (await api.post<TokenResponse>('/auth/registro', datos)).data }
