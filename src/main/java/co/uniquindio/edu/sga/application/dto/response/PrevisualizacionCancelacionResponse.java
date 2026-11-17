package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;

/** Penalidad y saldos estimados usando el reloj del servidor, antes de confirmar la cancelación. */
public record PrevisualizacionCancelacionResponse(long horasAntelacion, int tramo, BigDecimal porcentajeRetenido,
        BigDecimal penalidad, BigDecimal anticipoRequerido, BigDecimal pagosRegistrados,
        BigDecimal saldoAFavorEstimado, BigDecimal saldoPendienteEstimado) { }
