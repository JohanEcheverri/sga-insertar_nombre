import { useCallback, useState } from 'react'
import type { ReservaResumen } from '../modelos/reserva'
import {
  cancelarReserva, confirmarReserva, declararNoShow, registrarLlegada, registrarSalida,
} from '../servicios/reservaServicio'
import type { Avisos } from './useAviso'

export type AccionReserva = 'confirmar' | 'cancelar' | 'llegada' | 'no-show' | 'salida' | 'folio'

/** Ejecuta las acciones de recepción sobre una reserva, con confirmación para las destructivas. */
export function useAccionesReserva(avisos: Avisos, alTerminar: () => void, irAFolio: (codigo: string) => void) {
  const [ocupado, setOcupado] = useState<string | null>(null)
  const { exito, fallo } = avisos

  const ejecutar = useCallback(async (accion: AccionReserva, reserva: ReservaResumen) => {
    if (accion === 'folio') { irAFolio(reserva.codigo); return }
    if ((accion === 'cancelar' || accion === 'no-show') && !window.confirm('¿Confirmas esta acción? Se aplicará la política de cancelación congelada en la reserva.')) return
    setOcupado(reserva.codigo)
    try {
      if (accion === 'confirmar') await confirmarReserva(reserva.codigo)
      if (accion === 'cancelar') await cancelarReserva(reserva.codigo)
      if (accion === 'llegada') await registrarLlegada(reserva.codigo)
      if (accion === 'no-show') await declararNoShow(reserva.codigo)
      if (accion === 'salida') await registrarSalida(reserva.codigo)
      exito('Operación realizada correctamente.')
      alTerminar()
    } catch (causa) {
      fallo(causa)
    } finally {
      setOcupado(null)
    }
  }, [exito, fallo, alTerminar, irAFolio])

  return { ocupado, ejecutar }
}
