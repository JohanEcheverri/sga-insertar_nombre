/** Lee el claim `usuarioId` del JWT emitido por el backend (JwtService). */
export function obtenerUsuarioId(token: string | null | undefined): string | null {
  if (!token) return null
  try {
    const parte = token.split('.')[1]
    if (!parte) return null
    const carga = JSON.parse(atob(parte.replace(/-/g, '+').replace(/_/g, '/'))) as { usuarioId?: string }
    return carga.usuarioId ?? null
  } catch {
    return null
  }
}
