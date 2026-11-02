package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Información para reportar una novedad vinculada a un apartamento.
 * @param autor Persona que reporta la novedad.
 * @param descripcion Detalle del hecho reportado.
 * @param gravedad Nivel de gravedad asignado a la novedad.
 * @author Equipo SGA
 * @version 1.0
 */
public record RegistrarNovedadRequest(

        @NotBlank(message = "El autor de la novedad es obligatorio")
        String autor,

        @NotBlank(message = "La descripción de la novedad es obligatoria")
        String descripcion,

        @NotBlank(message = "La gravedad de la novedad es obligatoria")
        String gravedad
) { }
