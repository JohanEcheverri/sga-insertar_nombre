package co.uniquindio.edu.sga.application.usecase;

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

/** PUT /api/reservas/{codigo}/confirmar — RN-09 (hora de llegada) y RN-26 (anticipo cubierto). */
public class ConfirmarReservaUseCase {

    public record Command(UUID reservaId) { }

    public record Result(UUID reservaId, EstadoReserva estado, BigDecimal anticipoRequerido, BigDecimal pagado) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final NotificacionPort notificacion;

    public ConfirmarReservaUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                   FolioRepository folioRepository, NotificacionPort notificacion) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.notificacion = notificacion;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());

        BigDecimal anticipo = alojamiento.getConfiguracion().anticipoPara(reserva.getValorCongelado());
        reserva.confirmar(anticipo, folio.totalPagos());

        reservaRepository.guardar(reserva);
        Seguro.ejecutar("notificar reserva confirmada", () -> notificacion.reservaConfirmada(reserva));
        return new Result(reserva.getId().valor(), reserva.getEstado(), anticipo, folio.totalPagos());
    }
}
