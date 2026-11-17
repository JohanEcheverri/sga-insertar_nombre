import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertaNotificacion } from '../componentes/AlertaNotificacion'
import { PanelNovedades } from '../componentes/recepcion/PanelNovedades'
import { InsigniaEstado } from '../componentes/InsigniaEstado'
import { useAuth } from '../contexto/useAuth'
import { useAviso } from '../hooks/useAviso'
import type { ApartamentoResponse } from '../modelos/apartamento'
import { cambiarEstadoOperativo } from '../servicios/adminServicio'
import { listarTodosLosApartamentos } from '../servicios/apartamentoServicio'
import { listarPendientesPreparacion } from '../servicios/operacionServicio'

type Pestania = 'preparacion' | 'novedades'

export default function PanelServicio() {
  const { sesion } = useAuth()
  const { aviso, exito, error, fallo, limpiar } = useAviso()
  const avisos = useMemo(() => ({ exito, error, fallo }), [exito, error, fallo])
  const [pestania, setPestania] = useState<Pestania>('preparacion')
  const [apartamentos, setApartamentos] = useState<ApartamentoResponse[]>([])
  const [pendientes, setPendientes] = useState<ApartamentoResponse[]>([])
  const [cargando, setCargando] = useState(false)

  const cargar = useCallback(async () => {
    setCargando(true)
    try {
      const [p, a] = await Promise.all([listarPendientesPreparacion(), listarTodosLosApartamentos()])
      setPendientes(p.contenido)
      setApartamentos(a)
    } catch (causa) { fallo(causa) } finally { setCargando(false) }
  }, [fallo])

  useEffect(() => { void cargar() }, [cargar])

  const avanzar = async (a: ApartamentoResponse) => {
    const siguiente = a.estadoOperativo === 'PENDIENTE_PREPARACION' ? 'EN_PREPARACION' : 'PREPARADO'
    try {
      await cambiarEstadoOperativo(a.id, siguiente)
      exito(`Apartamento ${a.identificadorUnico}: ${siguiente === 'PREPARADO' ? 'alistamiento terminado' : 'alistamiento iniciado'}.`)
      await cargar()
    } catch (causa) { fallo(causa) }
  }

  return <main className="container py-4">
    <h1 className="h3">Panel de personal de servicio</h1>
    <p className="text-secondary">Preparación, aseo y reporte de novedades · tiempo estándar: 3 horas, de 12:00 p. m. a 3:00 p. m.</p>
    <ul className="nav nav-tabs mb-3">{([['preparacion', 'Preparación de apartamentos'], ['novedades', 'Novedades e incidencias']] as const).map(([key, label]) => <li className="nav-item" key={key}><button type="button" className={`nav-link${pestania === key ? ' active' : ''}`} onClick={() => { setPestania(key); limpiar() }}>{label}</button></li>)}</ul>
    <AlertaNotificacion mensaje={aviso?.mensaje} tipo={aviso?.tipo} alCerrar={limpiar} />
    {pestania === 'preparacion' && <section>
      <div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h5 mb-0">Pendientes de preparación</h2><button className="btn btn-outline-secondary btn-sm" type="button" onClick={() => void cargar()} disabled={cargando}>Actualizar</button></div>
      {cargando ? <p>Cargando apartamentos…</p> : pendientes.length === 0 ? <div className="alert alert-success">No hay apartamentos en preparación pendiente.</div>
        : <div className="row g-3">{pendientes.map((a) => <div className="col-12 col-md-6 col-xl-4" key={a.id}><article className="card h-100 shadow-sm"><div className="card-body">
          <div className="d-flex justify-content-between gap-2"><h3 className="h6">Apartamento {a.identificadorUnico}</h3><InsigniaEstado estado={a.estadoOperativo} /></div>
          <p className="text-secondary small">{a.descripcion}</p>
          <p className="small">{a.estadoOperativo === 'PENDIENTE_PREPARACION' ? 'Inicia el aseo y alistamiento. Tiempo estándar: 3 horas.' : 'Alistamiento en curso. Verifica limpieza, dotación y novedades antes de terminar.'}</p>
          <button className={`btn w-100 ${a.estadoOperativo === 'PENDIENTE_PREPARACION' ? 'btn-primary' : 'btn-success'}`} type="button" onClick={() => void avanzar(a)}>{a.estadoOperativo === 'PENDIENTE_PREPARACION' ? 'Iniciar preparación' : 'Marcar como preparado'}</button>
        </div></article></div>)}</div>}
    </section>}
    {pestania === 'novedades' && <PanelNovedades apartamentos={apartamentos} autorPorDefecto={sesion?.correo ?? ''} avisos={avisos} />}
  </main>
}
