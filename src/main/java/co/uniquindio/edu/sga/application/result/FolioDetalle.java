package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record FolioDetalle(UUID id, UUID reservaId, EstadoFolio estado, List<Movimiento> cargos, List<PagoDetalle> pagos,
                           BigDecimal totalCargos, BigDecimal totalPagos, BigDecimal saldo, BigDecimal saldoAFavor) {

    public record Movimiento(TipoCargo tipo, String concepto, double monto, boolean esCredito, LocalDateTime fecha) { }

    public record PagoDetalle(MedioPago medio, double monto, String referencia, LocalDateTime fecha) { }

    public static FolioDetalle de(Folio f) {
        return new FolioDetalle(f.getId().valor(), f.getReservaId().valor(), f.getEstado(),
                f.getCargos().stream().map(c -> new Movimiento(c.tipoCargo(), c.concepto(), c.monto().valor(),
                        c.tipoCargo().esCredito(), c.fechaRegistro())).toList(),
                f.getPagos().stream().map(p -> new PagoDetalle(p.medio(), p.monto().valor(), p.referencia(),
                        p.fechaRegistro())).toList(),
                f.totalCargos(), f.totalPagos(), f.saldo(), f.saldoAFavor());
    }
}
