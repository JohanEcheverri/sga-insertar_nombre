import { useCallback, useState } from 'react'
import { extraerMensajeError } from '../utilidades/errores'

export type TipoAviso = 'info' | 'exito' | 'error'
export interface Aviso { mensaje: string; tipo: TipoAviso }
export interface Avisos {
  exito: (mensaje: string) => void
  error: (mensaje: string) => void
  fallo: (causa: unknown) => void
}

export function useAviso() {
  const [aviso, setAviso] = useState<Aviso | null>(null)
  const exito = useCallback((mensaje: string) => setAviso({ mensaje, tipo: 'exito' }), [])
  const error = useCallback((mensaje: string) => setAviso({ mensaje, tipo: 'error' }), [])
  const fallo = useCallback((causa: unknown) => setAviso({ mensaje: extraerMensajeError(causa), tipo: 'error' }), [])
  const limpiar = useCallback(() => setAviso(null), [])
  return { aviso, exito, error, fallo, limpiar }
}
