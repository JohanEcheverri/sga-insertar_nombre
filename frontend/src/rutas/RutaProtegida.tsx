import { Navigate, Outlet } from 'react-router'
import type { Rol } from '../modelos/auth'
import { useAuth } from '../contexto/AuthContext'
export function RutaProtegida({ roles }: { roles?: Rol[] }) {
  const { autenticado, rol } = useAuth()
  if (!autenticado) return <Navigate to="/login" replace />
  if (roles && (!rol || !roles.includes(rol))) return <Navigate to="/" replace />
  return <Outlet />
}
