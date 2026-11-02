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

/** PUT /api/reservas/{codigo}/registrar-llegada — Check-in: RN-10 y RN-11. */
public class RegistrarLlegadaUseCase {

    public record Command(UUID reservaId) { }

    public record Result(UUID reservaId, EstadoReserva estadoReserva, EstadoOperativo estadoApartamento) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final EntregaApartamentoService entregaService;
    private final Clock clock;

    public RegistrarLlegadaUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                   EntregaApartamentoService entregaService, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.entregaService = entregaService;
        this.clock = clock;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        Apartamento apartamento = Cargas.apartamento(alojamiento, reserva.getApartamentoId().valor());

        entregaService.registrarLlegada(reserva, apartamento, LocalDate.now(clock));

        reservaRepository.guardar(reserva);
        alojamientoRepository.guardar(alojamiento);       // el estado operativo vive en el agregado Alojamiento
        return new Result(reserva.getId().valor(), reserva.getEstado(), apartamento.getEstadoOperativo());
    }
}
