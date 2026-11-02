package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Solicitud que registra quién autoriza el cierre del folio y la justificación.
 * @param motivo Justificación para cerrar el folio.
 * @author Equipo SGA
 * @version 1.0
 */
public record CerrarFolioRequest(

        @NotBlank(message = "El motivo del cierre es obligatorio")
        String motivo
) { }
