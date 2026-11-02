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

/** GET /api/reservas/llegadas — Llegadas previstas del día (reservas PENDIENTE o CONFIRMADA con esa entrada). */
public class ConsultarLlegadasPorDiaUseCase {

    public record Command(LocalDate dia, int pagina) { }

    private final ReservaRepository reservaRepository;

    public ConsultarLlegadasPorDiaUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Pagina<ReservaDetalle> ejecutar(Command cmd) {
        FiltroReservas filtro = new FiltroReservas(Set.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                null, null, null, null, cmd.dia(), null, null, null);
        return reservaRepository.buscar(filtro, cmd.pagina()).mapear(ReservaDetalle::de);
    }
}
