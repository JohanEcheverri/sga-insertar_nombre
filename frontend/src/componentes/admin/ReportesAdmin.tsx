import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import { esquemaRangoReporte } from '../../esquemas/admin'
import type { IndicadoresConsolidados, ReporteOcupacion } from '../../modelos/admin'
import { reporteConsolidado, reporteOcupacion } from '../../servicios/adminServicio'
import { contarReservasPorCanal } from '../../servicios/reservaServicio'
import { hoyISO, primerDiaDelMes, sumarDias } from '../../utilidades/fechas'
import { formatearMoneda } from '../../utilidades/formato'
import { validar } from '../../utilidades/validacion'

const CANALES = ['PORTAL', 'DIRECTO', 'EXTERNO']

function Indicador({ titulo, valor }: { titulo: string; valor: string }) {
  return <div className="col-6 col-md-4 col-xl-3"><div className="card h-100"><div className="card-body"><div className="text-secondary small">{titulo}</div><div className="fs-4 fw-semibold">{valor}</div></div></div></div>
}

export function ReportesAdmin({ avisos }: { avisos: Avisos }) {
  const [rango, setRango] = useState({ fechaInicio: primerDiaDelMes(), fechaFin: sumarDias(hoyISO(), 1) })
  const [errores, setErrores] = useState<Record<string, string>>({})
  const [consolidado, setConsolidado] = useState<IndicadoresConsolidados | null>(null)
  const [ocupacion, setOcupacion] = useState<ReporteOcupacion | null>(null)
  const [canales, setCanales] = useState<Record<string, number>>({})
  const [cargando, setCargando] = useState(false)
  const { fallo } = avisos

  const cargar = useCallback(async (inicio: string, fin: string) => {
    setCargando(true)
    try {
      const [c, o, porCanal] = await Promise.all([reporteConsolidado(inicio, fin), reporteOcupacion(inicio, fin), Promise.all(CANALES.map((k) => contarReservasPorCanal(k)))])
      setConsolidado(c)
      setOcupacion(o)
      setCanales(Object.fromEntries(CANALES.map((k, i) => [k, porCanal[i]])))
    } catch (causa) { fallo(causa) } finally { setCargando(false) }
  }, [fallo])

  useEffect(() => { void cargar(rango.fechaInicio, rango.fechaFin) }, [])

  const enviar = (e: FormEvent) => {
    e.preventDefault()
    const { datos, errores: errs } = validar(esquemaRangoReporte, rango)
    setErrores(errs)
    if (datos) void cargar(datos.fechaInicio, datos.fechaFin)
  }

  return <section>
    <form className="row g-2 align-items-end mb-4" onSubmit={enviar} noValidate>
      <div className="col-6 col-md-3"><label className="form-label small" htmlFor="rInicio">Desde</label>
        <input id="rInicio" type="date" className={`form-control${errores.fechaInicio ? ' is-invalid' : ''}`} value={rango.fechaInicio} onChange={(e) => setRango({ ...rango, fechaInicio: e.target.value })} />
        {errores.fechaInicio && <div className="invalid-feedback">{errores.fechaInicio}</div>}</div>
      <div className="col-6 col-md-3"><label className="form-label small" htmlFor="rFin">Hasta (exclusiva)</label>
        <input id="rFin" type="date" className={`form-control${errores.fechaFin ? ' is-invalid' : ''}`} value={rango.fechaFin} onChange={(e) => setRango({ ...rango, fechaFin: e.target.value })} />
        {errores.fechaFin && <div className="invalid-feedback">{errores.fechaFin}</div>}</div>
      <div className="col-12 col-md-2"><button className="btn btn-primary w-100" type="submit" disabled={cargando}>{cargando ? 'Consultando…' : 'Consultar'}</button></div>
    </form>

    {ocupacion && consolidado && <>
      <h2 className="h5">Ocupación e ingresos</h2>
      <div className="row g-3 mb-4">
        <Indicador titulo="Ocupación" valor={`${ocupacion.porcentajeOcupacion}%`} />
        <Indicador titulo="Noches vendidas" valor={String(ocupacion.nochesVendidas)} />
        <Indicador titulo="Noches disponibles" valor={String(ocupacion.nochesDisponibles)} />
        <Indicador titulo="Ingresos (pagos registrados)" valor={formatearMoneda(consolidado.ingresosTotales)} />
        <Indicador titulo="Reservas totales" valor={String(consolidado.reservasTotales)} />
        <Indicador titulo="Reservas activas" valor={String(consolidado.reservasActivas)} />
        <Indicador titulo="Reservas canceladas" valor={String(consolidado.reservasCanceladas)} />
      </div>
      <h2 className="h5">Reservas por canal de origen</h2>
      <p className="text-secondary small">Incluye canceladas y no-show; el conteo es el total histórico del canal.</p>
      <div className="row g-3">{CANALES.map((k) => <Indicador key={k} titulo={k.charAt(0) + k.slice(1).toLowerCase()} valor={String(canales[k] ?? 0)} />)}</div>
    </>}
  </section>
}
