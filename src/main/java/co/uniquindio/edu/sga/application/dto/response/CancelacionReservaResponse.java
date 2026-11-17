package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;

/** Liquidación informativa y movimiento contable generado al cancelar una reserva. */
public record CancelacionReservaResponse(
        String codigo,
        String estado,
        BigDecimal porcentajeRetenido,
        BigDecimal penalidad,
        BigDecimal saldoAFavor,
        BigDecimal saldoPendiente,
        ReservaDetalleResponse detalle) { }
