import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { useAccionesReserva } from '../../hooks/useAccionesReserva'
import type { Avisos } from '../../hooks/useAviso'
import type { FiltrosReservas, ReservaResumen } from '../../modelos/reserva'
import { listarReservas } from '../../servicios/reservaServicio'
import { Paginacion } from '../Paginacion'
import { TablaReservas } from '../TablaReservas'

interface Props {
  apartamentos: Record<string, string>
  avisos: Avisos
  irAFolio: (codigo: string) => void
}

const ESTADOS = ['PENDIENTE', 'CONFIRMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA', 'NO_SHOW']
const VACIO: FiltrosReservas = { estado: '', canal: '', documentoTitular: '', desde: '', hasta: '' }

/** Listado paginado de reservas (más recientes primero) con filtros. */
export function BuscadorReservas({ apartamentos, avisos, irAFolio }: Props) {
  const [borrador, setBorrador] = useState<FiltrosReservas>(VACIO)
  const [filtros, setFiltros] = useState<FiltrosReservas>(VACIO)
  const [pagina, setPagina] = useState(1)
  const [reservas, setReservas] = useState<ReservaResumen[]>([])
  const [totalPaginas, setTotalPaginas] = useState(1)
  const [cargando, setCargando] = useState(false)
  const { fallo, error } = avisos

  const cargar = useCallback(async () => {
    if ((filtros.desde && !filtros.hasta) || (!filtros.desde && filtros.hasta)) { error('Indica ambas fechas (desde y hasta) para filtrar por rango.'); return }
    setCargando(true)
    try {
      const respuesta = await listarReservas(filtros, pagina - 1)
      setReservas(respuesta.contenido)
      setTotalPaginas(respuesta.totalPaginas)
    } catch (causa) {
      fallo(causa)
    } finally {
      setCargando(false)
    }
  }, [filtros, pagina, fallo, error])

  useEffect(() => { void cargar() }, [cargar])
  const { ocupado, ejecutar } = useAccionesReserva(avisos, () => void cargar(), irAFolio)

  const aplicar = (evento: FormEvent) => { evento.preventDefault(); setPagina(1); setFiltros(borrador) }
  const cambiar = (clave: keyof FiltrosReservas, valor: string) => setBorrador({ ...borrador, [clave]: valor })

  return <section>
    <form className="row g-2 mb-3" onSubmit={aplicar}>
      <div className="col-6 col-md-2"><label className="form-label small" htmlFor="fEstado">Estado</label>
        <select id="fEstado" className="form-select" value={borrador.estado} onChange={(e) => cambiar('estado', e.target.value)}>
          <option value="">Todos</option>{ESTADOS.map((e) => <option key={e} value={e}>{e.replace('_', ' ')}</option>)}</select></div>
      <div className="col-6 col-md-2"><label className="form-label small" htmlFor="fCanal">Canal</label>
        <select id="fCanal" className="form-select" value={borrador.canal} onChange={(e) => cambiar('canal', e.target.value)}>
          <option value="">Todos</option><option value="PORTAL">Portal</option><option value="DIRECTO">Directo</option><option value="EXTERNO">Externo</option></select></div>
      <div className="col-12 col-md-2"><label className="form-label small" htmlFor="fDoc">Documento titular</label>
        <input id="fDoc" className="form-control" value={borrador.documentoTitular} onChange={(e) => cambiar('documentoTitular', e.target.value)} /></div>
      <div className="col-6 col-md-2"><label className="form-label small" htmlFor="fDesde">Desde</label>
        <input id="fDesde" type="date" className="form-control" value={borrador.desde} onChange={(e) => cambiar('desde', e.target.value)} /></div>
      <div className="col-6 col-md-2"><label className="form-label small" htmlFor="fHasta">Hasta</label>
        <input id="fHasta" type="date" className="form-control" value={borrador.hasta} onChange={(e) => cambiar('hasta', e.target.value)} /></div>
      <div className="col-12 col-md-2 d-flex align-items-end gap-2">
        <button className="btn btn-primary flex-grow-1" type="submit">Filtrar</button>
        <button className="btn btn-outline-secondary" type="button" onClick={() => { setBorrador(VACIO); setFiltros(VACIO); setPagina(1) }}>Limpiar</button></div>
    </form>
    {cargando ? <p className="text-secondary">Cargando…</p> : <TablaReservas reservas={reservas} apartamentos={apartamentos} ocupado={ocupado} alAccionar={(a, r) => void ejecutar(a, r)} />}
    <Paginacion pagina={pagina} totalPaginas={totalPaginas} alCambiar={setPagina} />
  </section>
}
