package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Representación de un cargo registrado en un folio.
 * @param tipoCargo Clasificación del cargo.
 * @param concepto Descripción del concepto cobrado.
 * @param monto Importe del cargo.
 * @param fechaRegistro Fecha y hora en que se registró.
 * @author Equipo SGA
 * @version 1.0
 */
public record CargoResponse(
        String tipoCargo,
        String concepto,
        BigDecimal monto,
        LocalDateTime fechaRegistro) { }
