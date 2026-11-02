package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Solicitud que registra quién autoriza el cierre del folio y la justificación.
 * @param autor Persona que autoriza el cierre.
 * @param motivo Justificación para cerrar el folio.
 * @param fecha Fecha y hora en que se emite la autorización.
 * @author Equipo SGA
 * @version 1.0
 */
public record CerrarFolioRequest(

        @NotBlank(message = "El autor de la autorización es obligatorio")
        String autor,

        @NotBlank(message = "El motivo del cierre es obligatorio")
        String motivo,

        @NotNull(message = "La fecha de autorización es obligatoria")
        LocalDateTime fecha
) { }
