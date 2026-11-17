import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { BuscadorReservas } from '../componentes/recepcion/BuscadorReservas'
import { OperacionDia } from '../componentes/recepcion/OperacionDia'
import { PanelFolio } from '../componentes/recepcion/PanelFolio'
import { PanelNovedades } from '../componentes/recepcion/PanelNovedades'
import { NuevaReservaDirecta } from '../componentes/recepcion/NuevaReservaDirecta'
import { useAuth } from '../contexto/useAuth'
import { useAviso } from '../hooks/useAviso'
import type { ApartamentoResponse } from '../modelos/apartamento'
import { listarTodosLosApartamentos } from '../servicios/apartamentoServicio'

type Pestania = 'dia' | 'reservas' | 'nueva' | 'folio' | 'novedades'
const PESTANIAS: [Pestania, string][] = [['dia', 'Llegadas y salidas'], ['reservas', 'Reservas'], ['nueva', 'Nueva reserva directa'], ['folio', 'Folio'], ['novedades', 'Novedades']]

export default function PanelRecepcion() {
  const { sesion, tieneRol } = useAuth()
  const { aviso, exito, error, fallo, limpiar } = useAviso()
  const avisos = useMemo(() => ({ exito, error, fallo }), [exito, error, fallo])
  const [pestania, setPestania] = useState<Pestania>('dia')
  const [codigoFolio, setCodigoFolio] = useState('')
  const [apartamentos, setApartamentos] = useState<ApartamentoResponse[]>([])

  useEffect(() => {
    listarTodosLosApartamentos().then(setApartamentos).catch(fallo)
  }, [fallo])

  const mapa = useMemo(() => Object.fromEntries(apartamentos.map((a) => [a.id, a.identificadorUnico])), [apartamentos])
  const irAFolio = useCallback((codigo: string) => { setCodigoFolio(codigo); setPestania('folio') }, [])

  return <main className="container py-4">
    <h1 className="h3">Panel de recepción</h1>
    <ul className="nav nav-tabs mb-3">{PESTANIAS.map(([clave, texto]) => <li className="nav-item" key={clave}>
      <button type="button" className={`nav-link${pestania === clave ? ' active' : ''}`} onClick={() => { setPestania(clave); limpiar() }}>{texto}</button></li>)}</ul>
    <AlertaNotificacion mensaje={aviso?.mensaje} tipo={aviso?.tipo} alCerrar={limpiar} />
    {pestania === 'dia' && <OperacionDia apartamentos={mapa} avisos={avisos} irAFolio={irAFolio} />}
    {pestania === 'reservas' && <BuscadorReservas apartamentos={mapa} avisos={avisos} irAFolio={irAFolio} />}
    {pestania === 'nueva' && <NuevaReservaDirecta apartamentos={apartamentos} avisos={avisos} />}
    {pestania === 'folio' && <PanelFolio codigoInicial={codigoFolio} avisos={avisos} />}
    {pestania === 'novedades' && <PanelNovedades apartamentos={apartamentos} autorPorDefecto={sesion?.correo ?? ''} avisos={avisos} soloLectura={!tieneRol(['ADMINISTRADOR'])} />}
  </main>
}
