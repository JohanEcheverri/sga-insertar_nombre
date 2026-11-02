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

/** PUT /api/reservas/{codigo}/registrar-salida — Check-out: RN-29 (folio cerrado). La reserva pasa a FINALIZADA. */
public class RegistrarSalidaUseCase {

    public record Command(UUID reservaId) { }

    public record Result(UUID reservaId, EstadoReserva estadoReserva, EstadoOperativo estadoApartamento) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final LiquidacionSalidaService liquidacionService;

    public RegistrarSalidaUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                  FolioRepository folioRepository, LiquidacionSalidaService liquidacionService) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.liquidacionService = liquidacionService;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        Apartamento apartamento = Cargas.apartamento(alojamiento, reserva.getApartamentoId().valor());

        liquidacionService.registrarSalida(reserva, folio, apartamento);

        reservaRepository.guardar(reserva);
        alojamientoRepository.guardar(alojamiento);
        return new Result(reserva.getId().valor(), reserva.getEstado(), apartamento.getEstadoOperativo());
    }
}
