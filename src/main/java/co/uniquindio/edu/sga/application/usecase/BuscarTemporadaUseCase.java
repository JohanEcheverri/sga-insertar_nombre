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

/** GET /api/temporadas — Temporadas configuradas (base primero, luego por fecha), paginadas. */
public class BuscarTemporadaUseCase {

    public record Command(int pagina) { }

    private final AlojamientoRepository alojamientoRepository;

    public BuscarTemporadaUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public Pagina<TemporadaDetalle> ejecutar(Command cmd) {
        List<TemporadaDetalle> lista = Cargas.alojamiento(alojamientoRepository).getTemporadas().stream()
                .sorted(Comparator.comparing((Temporada t) -> !t.esBase())
                        .thenComparing(t -> t.esBase() ? LocalDate.MIN : t.getRangoFechas().fechaInicio()))
                .map(TemporadaDetalle::de).toList();
        return Pagina.de(lista, cmd.pagina());
    }
}
