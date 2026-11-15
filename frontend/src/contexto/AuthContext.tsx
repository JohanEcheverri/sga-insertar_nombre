import { createContext, useContext, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import type { Rol, TokenResponse } from '../modelos/auth'
import { borrarSesion, guardarSesion, leerSesion } from '../servicios/sesion'

interface AuthContexto { sesion: TokenResponse | null; autenticado: boolean; rol: Rol | null; cerrarSesion: () => void; actualizarSesion: (sesion: TokenResponse | null) => void }
const ContextoAuth = createContext<AuthContexto | null>(null)
export function AuthProvider({ children }: { children: ReactNode }) {
  const [sesion, setSesion] = useState<TokenResponse | null>(() => leerSesion())
  const valor = useMemo<AuthContexto>(() => ({ sesion, autenticado: Boolean(sesion?.token), rol: sesion?.roles[0] ?? null,
    cerrarSesion: () => { borrarSesion(); setSesion(null) }, actualizarSesion: (nuevaSesion) => {
      if (nuevaSesion) guardarSesion(nuevaSesion)
      else borrarSesion()
      setSesion(nuevaSesion)
    } }), [sesion])
  return <ContextoAuth.Provider value={valor}>{children}</ContextoAuth.Provider>
}
export function useAuth(): AuthContexto { const contexto = useContext(ContextoAuth); if (!contexto) throw new Error('useAuth debe usarse dentro de AuthProvider.'); return contexto }
