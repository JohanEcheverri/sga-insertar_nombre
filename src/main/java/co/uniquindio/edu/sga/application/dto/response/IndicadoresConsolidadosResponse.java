package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Indicadores agregados del alojamiento para un período consultado.
 * @param fechaInicio Inicio del período del reporte.
 * @param fechaFin Fin del período del reporte.
 * @param ingresosTotales Ingresos acumulados durante el período.
 * @param ocupacionGlobal Porcentaje global de ocupación.
 * @param reservasTotales Cantidad total de reservas consideradas.
 * @param reservasActivas Cantidad de reservas activas.
 * @param reservasCanceladas Cantidad de reservas canceladas.
 * @author Equipo SGA
 * @version 1.0
 */
public record IndicadoresConsolidadosResponse(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal ingresosTotales,
        double ocupacionGlobal,
        long reservasTotales,
        long reservasActivas,
        long reservasCanceladas) { }
