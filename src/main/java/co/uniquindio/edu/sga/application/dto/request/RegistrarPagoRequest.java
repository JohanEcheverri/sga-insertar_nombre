package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/** Solicitud para registrar un pago en el folio de una reserva.
 * @param monto Valor positivo recibido.
 * @param medio Medio utilizado para realizar el pago.
 * @param referencia Código externo o comprobante opcional.
 * @author Equipo SGA
 * @version 1.0
 */
public record RegistrarPagoRequest(
        @NotNull(message = "El monto del pago es obligatorio") @Positive(message = "El monto debe ser positivo")
        BigDecimal monto,

        @NotBlank(message = "El medio de pago es obligatorio")
        String medio,

        String referencia
) { }
