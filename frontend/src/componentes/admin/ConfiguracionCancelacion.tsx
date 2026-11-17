import { useEffect, useState } from 'react'
import type { Avisos } from '../../hooks/useAviso'
import type { PoliticaCancelacionResponse, TramoPolitica } from '../../modelos/admin'
import { actualizarPoliticaCancelacion, obtenerPoliticaCancelacion } from '../../servicios/adminServicio'

const PREDETERMINADOS: TramoPolitica[] = [
  { horasAntelacionMinimas: 0, porcentajeRetencion: 0.25 },
  { horasAntelacionMinimas: 24, porcentajeRetencion: 0.25 },
  { horasAntelacionMinimas: 72, porcentajeRetencion: 0.10 },
]

export function ConfiguracionCancelacion({ avisos }: { avisos: Avisos }) {
  const [politica, setPolitica] = useState<PoliticaCancelacionResponse | null>(null)
  const [tramos, setTramos] = useState<TramoPolitica[]>(PREDETERMINADOS)
  const [noShow, setNoShow] = useState('100')
  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    obtenerPoliticaCancelacion().then((p) => {
      setPolitica(p)
      setTramos([...p.tramos].sort((a, b) => a.horasAntelacionMinimas - b.horasAntelacionMinimas))
      setNoShow(String(p.porcentajeRetencionNoShow * 100))
    }).catch((causa: unknown) => setError(causa instanceof Error ? causa.message : 'No se pudo cargar la política.')).finally(() => setCargando(false))
  }, [])

  const cambiar = (i: number, key: keyof TramoPolitica, valor: string) => setTramos((actuales) => actuales.map((t, j) => j === i ? { ...t, [key]: Number(valor) / (key === 'porcentajeRetencion' ? 100 : 1) } : t))
  const guardar = async () => {
    setError('')
    if (!politica || tramos.length < 2 || tramos[0].horasAntelacionMinimas !== 0
      || tramos.some((t, i) => t.horasAntelacionMinimas < 0 || (i > 0 && t.horasAntelacionMinimas <= tramos[i - 1].horasAntelacionMinimas)
        || t.porcentajeRetencion < 0 || t.porcentajeRetencion > 1)) {
      setError('Configura umbrales de horas ascendentes, comenzando en 0, y retenciones entre 0% y 100%.')
      return
    }
    setGuardando(true)
    try {
      const actualizada = await actualizarPoliticaCancelacion({ version: politica.version, tramos, porcentajeRetencionNoShow: Number(noShow) / 100 })
      setPolitica(actualizada)
      avisos.exito(`Política actualizada a ${actualizada.version}. Las reservas existentes conservan su versión congelada.`)
    } catch (causa) { avisos.fallo(causa) } finally { setGuardando(false) }
  }

  const etiquetas = ['Tramo de menor antelación', 'Tramo intermedio', 'Tramo de mayor antelación']
  return <section className="card shadow-sm"><div className="card-body">
    <h2 className="h5">Política de cancelación</h2>
    {cargando ? <p>Cargando política vigente…</p> : error ? <div className="alert alert-danger">{error}</div> : politica && <>
      <p className="text-secondary">Versión vigente: <strong>{politica.version}</strong>. Los umbrales son horas exactas antes del check-in (3:00 p. m.). Los cambios crean una nueva versión.</p>
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="table-responsive"><table className="table align-middle"><thead><tr><th>Tramo</th><th>Desde (horas)</th><th>Retención del total (%)</th></tr></thead><tbody>
        {tramos.map((t, i) => <tr key={i}><th>{etiquetas[i] ?? `Tramo ${i + 1}`}</th><td><input className="form-control" aria-label={`Umbral de horas tramo ${i + 1}`} type="number" min={0} value={t.horasAntelacionMinimas} onChange={(e) => cambiar(i, 'horasAntelacionMinimas', e.target.value)} /></td>
          <td><input className="form-control" aria-label={`Porcentaje tramo ${i + 1}`} type="number" min={0} max={100} step={0.01} value={t.porcentajeRetencion * 100} onChange={(e) => cambiar(i, 'porcentajeRetencion', e.target.value)} /></td></tr>)}
      </tbody></table></div>
      <p className="small text-secondary">En la configuración inicial: menos de 24 h retiene el anticipo (25%); entre 24 y 72 h, 25%; más de 72 h, 10%. Los umbrales se aplican usando la versión de política asociada a cada reserva.</p>
      <div className="row g-3 align-items-end"><div className="col-12 col-md-4"><label className="form-label" htmlFor="noShow">Retención por no-show (%)</label><input id="noShow" className="form-control" type="number" min={0} max={100} step={0.01} value={noShow} onChange={(e) => setNoShow(e.target.value)} /></div>
        <div className="col-12"><button className="btn btn-primary" type="button" disabled={guardando} onClick={() => void guardar()}>{guardando ? 'Guardando…' : 'Guardar política'}</button></div></div>
    </>}
  </div></section>
}
