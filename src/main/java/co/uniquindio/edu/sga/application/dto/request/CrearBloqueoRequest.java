package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Solicitud para bloquear un intervalo del calendario de un apartamento.
 * @param apartamentoId Identificador del apartamento que tendrá el bloqueo.
 * @param fechaInicio Primer día del intervalo bloqueado, inclusive.
 * @param fechaFin Fin del intervalo bloqueado, exclusivo.
 * @param motivo Razón por la que se bloquea el apartamento.
 * @author Equipo SGA
 * @version 1.0
 */
public record CrearBloqueoRequest(

        @NotBlank(message = "el bloqueo requiere el id de un aaprtamento")
        String apartamentoId,

        @NotNull(message = "La fecha inicial del bloqueo es obligatoria") @FutureOrPresent(message = "El bloqueo no puede iniciar en el pasado")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha final del bloqueo es obligatoria") @Future(message = "La fecha final debe ser futura")
        LocalDate fechaFin,

        @NotBlank(message = "El motivo del bloqueo es obligatorio")
        String motivo
) { }
