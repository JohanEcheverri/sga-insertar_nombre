import type { Cotizacion } from '../modelos/cotizacion'
import { formatearFecha } from '../utilidades/fechas'
import { formatearMoneda } from '../utilidades/formato'

export function DesgloseCotizacion({ cotizacion }: { cotizacion: Cotizacion }) {
  return <div>
    <div className="table-responsive">
      <table className="table table-sm align-middle mb-2">
        <thead>
          <tr>
            <th>Noche</th><th>Temporada</th>
            <th className="text-end">Tarifa</th><th className="text-end">Facturables</th><th className="text-end">Subtotal</th>
          </tr>
        </thead>
        <tbody>
          {cotizacion.lineas.map((l) => <tr key={l.noche}>
            <td>{formatearFecha(l.noche)}</td>
            <td>{l.temporada}</td>
            <td className="text-end">{formatearMoneda(l.tarifa)}</td>
            <td className="text-end">{l.ocupantesFacturables}</td>
            <td className="text-end">{formatearMoneda(l.subtotal)}</td>
          </tr>)}
        </tbody>
        <tfoot>
          <tr>
            <th colSpan={4}>Total ({cotizacion.lineas.length} {cotizacion.lineas.length === 1 ? 'noche' : 'noches'})</th>
            <th className="text-end">{formatearMoneda(cotizacion.total)}</th>
          </tr>
        </tfoot>
      </table>
    </div>
    <p className="small text-secondary mb-0">
      Los ocupantes por debajo del umbral de edad facturable cuentan para la capacidad, pero no generan cargo.
    </p>
  </div>
}
