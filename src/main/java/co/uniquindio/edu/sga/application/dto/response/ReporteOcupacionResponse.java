package co.uniquindio.edu.sga.application.dto.response;
import java.math.BigDecimal;

import java.time.LocalDate;

/** Reporte de ocupación del alojamiento para un intervalo de fechas.
 * @param fechaInicio Inicio del intervalo consultado.
 * @param fechaFin Fin del intervalo consultado.
 * @param porcentajeOcupacion Porcentaje de noches disponibles que fueron vendidas.
 * @param nochesVendidas Total de noches ocupadas por reservas.
 * @param nochesDisponibles Total de noches disponibles para la venta.
 * @param apartamentosIncluidos Cantidad de apartamentos considerados en el cálculo.
 * @author Equipo SGA
 * @version 1.0
 */
public record ReporteOcupacionResponse(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal porcentajeOcupacion,
        long nochesVendidas,
        long nochesDisponibles,
        int apartamentosIncluidos) { }
