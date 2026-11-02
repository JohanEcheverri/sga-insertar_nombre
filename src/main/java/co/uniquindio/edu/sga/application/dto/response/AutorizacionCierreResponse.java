package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDateTime;

/** Datos de auditoría de la autorización asociada al cierre de un folio.
 * @param autor Persona que autorizó el cierre.
 * @param motivo Justificación registrada para el cierre.
 * @param fecha Fecha y hora de autorización.
 * @author Equipo SGA
 * @version 1.0
 */
public record AutorizacionCierreResponse(
        String autor,
        String motivo,
        LocalDateTime fecha) { }
