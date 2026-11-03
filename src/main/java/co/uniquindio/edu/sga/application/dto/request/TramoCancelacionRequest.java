package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Umbral y porcentaje de retención aplicados a una cancelación.
 * @param diasAntelacionMinimos Antelación mínima, en días, para aplicar el tramo.
 * @param porcentajeRetencion Fracción del valor de la reserva que se retiene, entre 0 y 1.
 * @author Equipo SGA
 * @version 1.0
 */
public record TramoCancelacionRequest(

        @NotNull(message = "Los días mínimos de antelación son obligatorios") @PositiveOrZero(message = "Los días de antelación no pueden ser negativos")
        Integer diasAntelacionMinimos,

        @NotNull(message = "El porcentaje de retención es obligatorio") @DecimalMin(value = "0.0", message = "La retención mínima es 0") @DecimalMax(value = "1.0", message = "La retención máxima es 1")
        BigDecimal porcentajeRetencion
) { }
