import { useCallback, useEffect, useState } from 'react'
import { useAccionesReserva } from '../../hooks/useAccionesReserva'
import type { Avisos } from '../../hooks/useAviso'
import type { ReservaResumen } from '../../modelos/reserva'
import { llegadasDelDia, salidasDelDia } from '../../servicios/reservaServicio'
import { hoyISO } from '../../utilidades/fechas'
import { Paginacion } from '../Paginacion'
import { TablaReservas } from '../TablaReservas'

interface Props {
  apartamentos: Record<string, string>
  avisos: Avisos
  irAFolio: (codigo: string) => void
}

/** Llegadas y salidas previstas de un día, con check-in/out directo. */
export function OperacionDia({ apartamentos, avisos, irAFolio }: Props) {
  const [fecha, setFecha] = useState(hoyISO())
  const [llegadas, setLlegadas] = useState<ReservaResumen[]>([])
  const [salidas, setSalidas] = useState<ReservaResumen[]>([])
  const [pagLlegadas, setPagLlegadas] = useState(1)
  const [pagSalidas, setPagSalidas] = useState(1)
  const [totalLlegadas, setTotalLlegadas] = useState(1)
  const [totalSalidas, setTotalSalidas] = useState(1)
  const [cargando, setCargando] = useState(false)
  const { fallo } = avisos

  const cargar = useCallback(async () => {
    setCargando(true)
    try {
      const [l, s] = await Promise.all([llegadasDelDia(fecha, pagLlegadas - 1), salidasDelDia(fecha, pagSalidas - 1)])
      setLlegadas(l.contenido); setTotalLlegadas(l.totalPaginas)
      setSalidas(s.contenido); setTotalSalidas(s.totalPaginas)
    } catch (causa) {
      fallo(causa)
    } finally {
      setCargando(false)
    }
  }, [fecha, pagLlegadas, pagSalidas, fallo])

  useEffect(() => { void cargar() }, [cargar])
  const { ocupado, ejecutar } = useAccionesReserva(avisos, () => void cargar(), irAFolio)

  return <section>
    <div className="d-flex align-items-end gap-3 mb-3">
      <div><label className="form-label" htmlFor="fechaDia">Día</label>
        <input id="fechaDia" type="date" className="form-control" value={fecha} onChange={(e) => { setFecha(e.target.value); setPagLlegadas(1); setPagSalidas(1) }} /></div>
      <button className="btn btn-outline-secondary" type="button" onClick={() => void cargar()} disabled={cargando}>{cargando ? 'Actualizando…' : 'Actualizar'}</button>
    </div>
    <div className="row g-4">
      <div className="col-12 col-xl-6"><h2 className="h5">Llegadas previstas</h2>
        <TablaReservas reservas={llegadas} apartamentos={apartamentos} ocupado={ocupado} alAccionar={(a, r) => void ejecutar(a, r)} />
        <Paginacion pagina={pagLlegadas} totalPaginas={totalLlegadas} alCambiar={setPagLlegadas} /></div>
      <div className="col-12 col-xl-6"><h2 className="h5">Salidas previstas</h2>
        <p className="text-secondary small">La salida exige el folio cerrado (RN-29): ciérralo desde el botón Folio.</p>
        <TablaReservas reservas={salidas} apartamentos={apartamentos} ocupado={ocupado} alAccionar={(a, r) => void ejecutar(a, r)} />
        <Paginacion pagina={pagSalidas} totalPaginas={totalSalidas} alCambiar={setPagSalidas} /></div>
    </div>
  </section>
}
