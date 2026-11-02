package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDate;

/** Vista de salida de un bloqueo de calendario.
 * @param id Identificador del bloqueo.
 * @param apartamentoId Identificador del apartamento bloqueado.
 * @param fechaInicio Primer día bloqueado, inclusive.
 * @param fechaFin Fin del intervalo bloqueado, exclusivo.
 * @param motivo Motivo del bloqueo.
 * @param activo Indica si el bloqueo sigue vigente.
 * @author Equipo SGA
 * @version 1.0
 */
public record BloqueoResponse(
        String id,
        String apartamentoId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String motivo,
        boolean activo) { }
