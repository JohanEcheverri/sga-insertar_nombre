package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Solicitud para añadir un cargo al folio de una reserva.
 * @param tipoCargo Clasificación del cargo.
 * @param concepto Descripción del concepto cobrado.
 * @param monto Importe positivo que se cargará al folio.
 * @author Equipo SGA
 * @version 1.0
 */
public record RegistrarCargoRequest(

        @NotBlank(message = "El tipo de cargo es obligatorio")
        String tipoCargo,

        @NotBlank(message = "El concepto del cargo es obligatorio")
        String concepto,

        @NotNull(message = "El monto del cargo es obligatorio") @Positive(message = "El monto debe ser positivo")
        BigDecimal monto
) { }
