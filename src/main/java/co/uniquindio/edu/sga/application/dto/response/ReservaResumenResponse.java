package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Vista breve de una reserva para listados e historiales.
 * @param codigo Identificador de la reserva.
 * @param nombreTitular Nombres del titular.
 * @param apellidoTitular Apellidos del titular.
 * @param apartamentoId Identificador del apartamento reservado.
 * @param fechaEntrada Fecha de llegada.
 * @param fechaSalida Fecha de salida.
 * @param estado Estado actual de la reserva.
 * @param valorCongelado Precio conservado al confirmar la reserva.
 * @author Equipo SGA
 * @version 1.0
 */
public record ReservaResumenResponse(
        String codigo,
        String nombreTitular,
        String apellidoTitular,
        String apartamentoId,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String estado,
        BigDecimal valorCongelado) { }
