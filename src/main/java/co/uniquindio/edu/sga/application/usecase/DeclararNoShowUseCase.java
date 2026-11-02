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

/** PUT /api/reservas/{codigo}/no-show — Desde la hora límite del día de entrada; penalidad de la política congelada. */
public class DeclararNoShowUseCase {

    public record Command(UUID reservaId) { }

    public record Result(UUID reservaId, EstadoReserva estado, double porcentajeRetenido, BigDecimal penalidad,
                         BigDecimal saldoAFavor, BigDecimal saldoPendiente) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final NoShowProcesamientoServicio noShowService;
    private final Clock clock;

    public DeclararNoShowUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                 FolioRepository folioRepository, NoShowProcesamientoServicio noShowService, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.noShowService = noShowService;
        this.clock = clock;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());

        var r = noShowService.ejecutarNoShow(reserva, folio, alojamiento.getConfiguracion(), LocalDateTime.now(clock));

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        return new Result(reserva.getId().valor(), reserva.getEstado(), r.porcentajeRetenido(), r.penalidad(),
                r.saldoAFavor(), r.saldoPendiente());
    }
}
