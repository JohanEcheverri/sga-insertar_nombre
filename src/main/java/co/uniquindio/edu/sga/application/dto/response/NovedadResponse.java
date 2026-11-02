package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDateTime;

/** Vista de salida de una novedad reportada en un apartamento.
 * @param id Identificador de la novedad.
 * @param apartamentoId Identificador del apartamento relacionado.
 * @param autor Persona que registró la novedad.
 * @param descripcion Descripción del hecho reportado.
 * @param gravedad Nivel de gravedad.
 * @param fechaReporte Fecha y hora del reporte.
 * @author Equipo SGA
 * @version 1.0
 */
public record NovedadResponse(
        String id,
        String apartamentoId,
        String autor,
        String descripcion,
        String gravedad,
        LocalDateTime fechaReporte) { }
