package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Solicitud para crear una temporada tarifaria con un rango de fechas.
 * @param nombre Nombre identificador de la temporada.
 * @param fechaInicio Primer día en que aplica la temporada.
 * @param fechaFin Día final exclusivo del rango de vigencia.
 * @author Equipo SGA
 * @version 1.0
 */
public record CrearTemporadaRequest(

        @NotBlank(message = "El nombre de la temporada es obligatorio")
        String nombre,

        @NotNull(message = "La fecha inicial de la temporada es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha final de la temporada es obligatoria")
        LocalDate fechaFin
) { }
