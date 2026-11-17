import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { GestionApartamentos } from '../componentes/admin/GestionApartamentos'
import { GestionBloqueos } from '../componentes/admin/GestionBloqueos'
import { GestionTarifas } from '../componentes/admin/GestionTarifas'
import { ReportesAdmin } from '../componentes/admin/ReportesAdmin'
import PanelRecepcion from './PanelRecepcion'
import { GestionUsuarios } from '../componentes/admin/GestionUsuarios'
import { ConfiguracionCancelacion } from '../componentes/admin/ConfiguracionCancelacion'
import { useAviso } from '../hooks/useAviso'
import type { ApartamentoResponse } from '../modelos/apartamento'
import type { TemporadaResponse } from '../modelos/admin'
import { listarTemporadas } from '../servicios/adminServicio'
import { listarTodosLosApartamentos } from '../servicios/apartamentoServicio'

type Pestania = 'recepcion' | 'apartamentos' | 'tarifas' | 'bloqueos' | 'usuarios' | 'politica' | 'reportes'
const PESTANIAS: [Pestania, string][] = [['recepcion', 'Operación / Recepción'], ['apartamentos', 'Apartamentos'], ['tarifas', 'Temporadas y tarifas'], ['bloqueos', 'Bloqueos'], ['usuarios', 'Usuarios'], ['politica', 'Cancelación'], ['reportes', 'Reportes']]

export default function PanelAdmin() {
  const { aviso, exito, error, fallo, limpiar } = useAviso()
  const avisos = useMemo(() => ({ exito, error, fallo }), [exito, error, fallo])
  const [pestania, setPestania] = useState<Pestania>('recepcion')
  const [apartamentos, setApartamentos] = useState<ApartamentoResponse[]>([])
  const [temporadas, setTemporadas] = useState<TemporadaResponse[]>([])

  const recargar = useCallback(async () => {
    try {
      const [aptos, temps] = await Promise.all([listarTodosLosApartamentos(), listarTemporadas()])
      setApartamentos(aptos)
      setTemporadas(temps)
    } catch (causa) { fallo(causa) }
  }, [fallo])

  useEffect(() => { void recargar() }, [recargar])

  return <main className="container py-4">
    <h1 className="h3">Panel de administración</h1>
    <ul className="nav nav-tabs mb-3">{PESTANIAS.map(([clave, texto]) => <li className="nav-item" key={clave}>
      <button type="button" className={`nav-link${pestania === clave ? ' active' : ''}`} onClick={() => { setPestania(clave); limpiar() }}>{texto}</button></li>)}</ul>
    <AlertaNotificacion mensaje={aviso?.mensaje} tipo={aviso?.tipo} alCerrar={limpiar} />
    {pestania === 'recepcion' && <PanelRecepcion />}
    {pestania === 'apartamentos' && <GestionApartamentos apartamentos={apartamentos} temporadas={temporadas} avisos={avisos} alCambiar={() => void recargar()} />}
    {pestania === 'tarifas' && <GestionTarifas apartamentos={apartamentos} temporadas={temporadas} avisos={avisos} alCambiar={() => void recargar()} />}
    {pestania === 'bloqueos' && <GestionBloqueos apartamentos={apartamentos} avisos={avisos} />}
    {pestania === 'usuarios' && <GestionUsuarios avisos={avisos} />}
    {pestania === 'politica' && <ConfiguracionCancelacion avisos={avisos} />}
    {pestania === 'reportes' && <ReportesAdmin avisos={avisos} />}
  </main>
}
