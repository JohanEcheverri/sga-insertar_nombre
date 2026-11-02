package co.uniquindio.edu.sga.application.dto.response;

import java.util.List;

/** Vista de salida de una versión de política de cancelación.
 * @param version Identificador de la versión vigente o consultada.
 * @param tramos Umbrales y retenciones configurados para cancelar.
 * @param porcentajeRetencionNoShow Retención aplicable ante un no-show.
 * @author Equipo SGA
 * @version 1.0
 */
public record PoliticaCancelacionResponse(
        String version,
        List<TramoCancelacionResponse> tramos,
        double porcentajeRetencionNoShow) { }
