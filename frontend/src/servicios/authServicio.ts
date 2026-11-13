import type { LoginRequest, TokenResponse } from '../modelos/auth'
import { api } from './api'
export async function iniciarSesion(datos: LoginRequest): Promise<TokenResponse> { return (await api.post<TokenResponse>('/auth/login', datos)).data }
