import axios from 'axios'
import { borrarSesion, obtenerToken } from './sesion'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL?.trim() || 'http://localhost:8080/api',
  timeout: 70_000,
})

api.interceptors.request.use((config) => {
  const token = obtenerToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use((respuesta) => respuesta, (error: unknown) => {
  if (axios.isAxiosError(error) && error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
    borrarSesion()
    if (window.location.pathname !== '/login') window.location.assign('/login')
  }
  return Promise.reject(error)
})
