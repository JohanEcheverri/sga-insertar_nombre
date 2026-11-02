package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** Datos para registrar la salida del huésped y finalizar su estancia.
 * @param fechaHoraSalida Fecha y hora efectiva del check-out.
 * @author Equipo SGA
 * @version 1.0
 */
public record RegistrarSalidaRequest(

        @NotNull(message = "La fecha y hora de salida son obligatorias")
        LocalDateTime fechaHoraSalida
) { }
