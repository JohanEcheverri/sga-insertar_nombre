package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Datos para actualizar nombre y vigencia de una temporada.
 * @param nombre Nombre actualizado de la temporada.
 * @param fechaInicio Nuevo primer día de vigencia.
 * @param fechaFin Nuevo día final exclusivo de vigencia.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarTemporadaRequest(

        @NotBlank(message = "El nombre de la temporada es obligatorio")
        String nombre,

        @NotNull(message = "La fecha inicial de la temporada es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha final de la temporada es obligatoria")
        LocalDate fechaFin
) { }
