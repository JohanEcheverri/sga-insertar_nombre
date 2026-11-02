package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Solicitud para cambiar el estado operativo de un apartamento.
 * @param estadoOperativo Nombre del nuevo estado operativo.
 * @author Equipo SGA
 * @version 1.0
 */
public record CambiarEstadoApartamentoRequest(

        @NotBlank(message = "El estado operativo es obligatorio")
        String estadoOperativo
) { }
