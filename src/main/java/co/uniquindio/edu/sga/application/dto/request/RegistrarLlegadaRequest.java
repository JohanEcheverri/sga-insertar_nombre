package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Datos para registrar la llegada del huésped asociado a una reserva.
 * @param fechaHoraLlegada Fecha y hora efectiva del check-in.
 * @author Equipo SGA
 * @version 1.0
 */
public record RegistrarLlegadaRequest(

        @NotNull(message = "La fecha y hora de llegada son obligatorias")
        LocalDateTime fechaHoraLlegada
) { }
