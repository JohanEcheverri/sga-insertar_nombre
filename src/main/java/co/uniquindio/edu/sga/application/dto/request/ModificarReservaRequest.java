package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Datos editables para modificar fechas o apartamento de una reserva.
 * @param apartamentoId Identificador del apartamento asignado.
 * @param fechaEntrada Nueva fecha de entrada.
 * @param fechaSalida Nueva fecha de salida.
 * @param identificadorExterno Código opcional asociado al canal externo.
 * @param horaEstimadaLlegada Nueva hora prevista de llegada.
 * @param ocupantes Lista actualizada de ocupantes de la reserva.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarReservaRequest(

        @NotBlank(message = "El identificador del apartamento es obligatorio")
        String apartamentoId,

        @NotNull(message = "La fecha de entrada es obligatoria") @FutureOrPresent(message = "La entrada no puede ser anterior a hoy")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria") @Future(message = "La salida debe ser futura")
        LocalDate fechaSalida,

        String identificadorExterno,

        @NotNull(message = "La hora estimada de llegada es obligatoria")
        LocalTime horaEstimadaLlegada,

        @NotEmpty(message = "La reserva debe incluir al menos un ocupante") List<@Valid OcupanteRequest> ocupantes
) { }
