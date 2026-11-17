package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pago o abono registrado en el folio. */
public record PagoResponse(String medio, BigDecimal monto, String referencia, LocalDateTime fechaRegistro) { }
