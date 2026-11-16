import { Navigate, Outlet } from 'react-router'
import type { RolUsuario } from '../modelos/usuario'
import { useAuth } from '../contexto/useAuth'

interface PropiedadesRutaProtegida {
  rolesPermitidos?: RolUsuario[]
}

export function RutaProtegida({ rolesPermitidos }: PropiedadesRutaProtegida) {
  const { estaAutenticado, tieneRol } = useAuth()
  if (!estaAutenticado) return <Navigate to="/login" replace />
  if (rolesPermitidos && !tieneRol(rolesPermitidos)) return <Navigate to="/" replace />
  return <Outlet />
}
