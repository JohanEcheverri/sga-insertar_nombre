package co.uniquindio.edu.sga.application.dto.response;

/** Tramo de retención incluido en la respuesta de una política de cancelación.
 * @param diasAntelacionMinimos Antelación mínima para aplicar el porcentaje.
 * @param porcentajeRetencion Fracción del valor que se retiene, entre 0 y 1.
 * @author Equipo SGA
 * @version 1.0
 */
public record TramoCancelacionResponse(
        int diasAntelacionMinimos,
        double porcentajeRetencion) { }
