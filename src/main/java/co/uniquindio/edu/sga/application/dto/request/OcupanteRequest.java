package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Datos de una persona alojada como parte de una reserva.
 * @param nombre Nombres del ocupante.
 * @param apellido Apellidos del ocupante.
 * @param documento Número de documento de identidad.
 * @param fechaNacimiento Fecha de nacimiento del ocupante.
 * @author Equipo SGA
 * @version 1.0
 */
public record OcupanteRequest(

        @NotBlank(message = "El nombre del ocupante es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido del ocupante es obligatorio")
        String apellido,

        @NotBlank(message = "El documento del ocupante es obligatorio")
        String documento,

        @NotNull(message = "La fecha de nacimiento del ocupante es obligatoria")
        LocalDate fechaNacimiento
) { }
