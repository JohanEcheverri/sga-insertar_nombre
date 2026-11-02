package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.Cargo;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;
import co.uniquindio.edu.sga.domain.valueobject.EstadoFolio;
import co.uniquindio.edu.sga.domain.valueobject.TipoCargo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Coordina Reserva y Folio (dos agregados) al cancelar una reserva o al vencer su plazo de
 * confirmación. RN-13: la retención sale de la política CONGELADA en la reserva, no de la vigente.
 * Opera sobre los agregados ya cargados; la persistencia la hace el caso de uso.
 */
public class CancelacionReservaService {

    /**
     * @param porcentajeRetenido porcentaje de retención aplicado (0..1)
     * @param penalidad          cargo registrado por la retención
     * @param saldoAFavor        monto a devolver al huésped (se registra; el desembolso es externo, 3.4)
     * @param saldoPendiente     deuda restante del huésped (penalidad mayor a lo pagado)
     */
    public record Resultado(double porcentajeRetenido, BigDecimal penalidad,
                            BigDecimal saldoAFavor, BigDecimal saldoPendiente) { }

    /** Cancelación solicitada por el huésped o por recepción. */
    public Resultado procesarCancelacion(Reserva reserva, Folio folio, LocalDateTime ahora) {
        exigirFolioDeLaReserva(reserva, folio);
        long diasAntelacion = Math.max(0,
                ChronoUnit.DAYS.between(ahora.toLocalDate(), reserva.getEstancia().fechaEntrada()));
        double porcentaje = reserva.getPoliticaCancelacion().retencionParaDias(diasAntelacion);
        reserva.cancelar();                                    // RN-08: solo PENDIENTE o CONFIRMADA
        return liquidar(reserva, folio, porcentaje, TipoCargo.PENALIDAD_CANCELACION,
                "Penalidad por cancelación (política " + reserva.getPoliticaCancelacion().version().valor() + ")", ahora);
    }

    /** RN-21 - Cancelación automática por vencimiento del plazo de confirmación: sin penalidad. */
    public Resultado procesarVencimiento(Reserva reserva, Folio folio, LocalDateTime ahora) {
        exigirFolioDeLaReserva(reserva, folio);
        reserva.cancelar();
        return liquidar(reserva, folio, 0.0, TipoCargo.PENALIDAD_CANCELACION, "Vencimiento", ahora);
    }

    // ---- compartido con NoShowProcesamientoServicio (mismo paquete) ----

    static void exigirFolioDeLaReserva(Reserva reserva, Folio folio) {
        if (!folio.getReservaId().equals(reserva.getId())) {
            throw new ReglaDominioException("El folio no pertenece a la reserva indicada.");
        }
        if (folio.getEstado() != EstadoFolio.ABIERTO) {
            throw new ReglaDominioException("El folio de la reserva debe estar ABIERTO para liquidarla.");
        }
    }

    /**
     * RN-16 - Sin editar ni borrar: se registra un reverso del alojamiento (movimiento inverso) y,
     * si corresponde, el cargo de penalidad. El saldo a favor resultante es la devolución.
     */
    static Resultado liquidar(Reserva reserva, Folio folio, double porcentaje, TipoCargo tipoPenalidad,
                              String conceptoPenalidad, LocalDateTime ahora) {
        folio.registrarCargo(new Cargo(TipoCargo.REVERSO_ALOJAMIENTO,
                "Reverso del alojamiento", reserva.getValorCongelado(), ahora));
        BigDecimal penalidad = reserva.getValorCongelado().valor()
                .multiply(BigDecimal.valueOf(porcentaje))
                .setScale(0, RoundingMode.HALF_UP);
        if (penalidad.signum() > 0) {
            folio.registrarCargo(new Cargo(tipoPenalidad, conceptoPenalidad,
                    new Dinero(penalidad), ahora));
        }
        return new Resultado(porcentaje, penalidad, folio.saldoAFavor(), folio.saldoPendiente());
    }
}
