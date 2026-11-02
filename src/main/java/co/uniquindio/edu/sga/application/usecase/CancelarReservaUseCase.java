package co.uniquindio.edu.sga.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import co.uniquindio.edu.sga.application.command.*;
import co.uniquindio.edu.sga.application.exception.*;
import co.uniquindio.edu.sga.application.port.*;
import co.uniquindio.edu.sga.application.result.*;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.*;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.exception.*;
import co.uniquindio.edu.sga.domain.repository.*;
import co.uniquindio.edu.sga.domain.service.*;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PUT /api/reservas/{codigo}/cancelar — Retención según la política CONGELADA (RN-13), registrada
 * como cargo y saldo a favor en el folio. Libera las noches de inmediato (RN-12: ya no está activa).
 */
@Service
@Transactional
public class CancelarReservaUseCase {

    public record Command(UUID reservaId, Solicitante solicitante) { }

    public record Result(UUID reservaId, EstadoReserva estado, double porcentajeRetenido, BigDecimal penalidad,
                         BigDecimal saldoAFavor, BigDecimal saldoPendiente, ReservaDetalle detalle) { }

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final CancelacionReservaService cancelacionService;
    private final NotificacionPort notificacion;
    private final Clock clock;

    public CancelarReservaUseCase(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                  CancelacionReservaService cancelacionService, NotificacionPort notificacion, Clock clock) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.cancelacionService = cancelacionService;
        this.notificacion = notificacion;
        this.clock = clock;
    }

    public Result ejecutar(Command cmd) {
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        cmd.solicitante().exigirAccesoA(reserva);                      // un huésped solo cancela la suya
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());

        var r = cancelacionService.procesarCancelacion(reserva, folio, LocalDateTime.now(clock));

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        Seguro.ejecutar("notificar reserva cancelada", () -> notificacion.reservaCancelada(reserva));
        return new Result(reserva.getId().valor(), reserva.getEstado(), r.porcentajeRetenido(), r.penalidad(),
                r.saldoAFavor(), r.saldoPendiente(), ReservaDetalle.de(reserva));
    }
}
