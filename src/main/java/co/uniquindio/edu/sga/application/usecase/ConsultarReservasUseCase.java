package co.uniquindio.edu.sga.application.usecase;

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

/** GET /api/reservas — Listado paginado (10 por página) con filtros; solo personal interno (lo protege el endpoint). */
@Service
public class ConsultarReservasUseCase {

    public record Command(FiltroReservas filtro, int pagina) { }

    private final ReservaRepository reservaRepository;

    public ConsultarReservasUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Pagina<ReservaDetalle> ejecutar(Command cmd) {
        FiltroReservas filtro = cmd.filtro() == null ? FiltroReservas.sinFiltros() : cmd.filtro();
        return reservaRepository.buscar(filtro, cmd.pagina()).mapear(ReservaDetalle::de);
    }
}
