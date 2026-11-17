package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.result.FolioDetalle;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.Cargas;
import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.service.CancelacionReservaService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/** Calcula la liquidación vigente que se aplicaría si la reserva se cancela ahora. */
@Service
public class ConsultarPenalidadCancelacionUseCase {
    public record Resultado(long horasAntelacion, int tramo, BigDecimal porcentajeRetenido,
                            BigDecimal penalidad, BigDecimal anticipoRequerido,
                            BigDecimal pagosRegistrados, BigDecimal saldoAFavorEstimado,
                            BigDecimal saldoPendienteEstimado) { }
    public record Command(UUID reservaId, Solicitante solicitante) { }

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final Clock clock;

    public ConsultarPenalidadCancelacionUseCase(ReservaRepository reservaRepository, FolioRepository folioRepository, Clock clock) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.clock = clock;
    }

    public Resultado ejecutar(Command cmd) {
        if (cmd.solicitante() == null) throw new AccesoDenegadoException("Debe autenticarse para consultar la cancelación.");
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        cmd.solicitante().exigirAccesoA(reserva);
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        LocalDateTime ahora = LocalDateTime.now(clock);
        long horas = CancelacionReservaService.horasAntelacion(reserva, ahora);
        BigDecimal porcentaje = CancelacionReservaService.porcentajeRetencion(reserva, ahora);
        BigDecimal penalidad = reserva.getValorCongelado().valor().multiply(porcentaje).setScale(0, RoundingMode.HALF_UP);
        BigDecimal saldoFinal = folio.saldo().subtract(reserva.getValorCongelado().valor()).add(penalidad);
        return new Resultado(horas, CancelacionReservaService.tramoCancelacion(reserva, ahora), porcentaje, penalidad,
                reserva.getValorCongelado().valor().multiply(new BigDecimal("0.25")).setScale(0, RoundingMode.HALF_UP),
                folio.totalPagos(), saldoFinal.negate().max(BigDecimal.ZERO), saldoFinal.max(BigDecimal.ZERO));
    }
}
