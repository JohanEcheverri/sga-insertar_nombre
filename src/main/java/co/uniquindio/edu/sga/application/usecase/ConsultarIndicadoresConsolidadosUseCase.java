package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.support.Cargas;
import co.uniquindio.edu.sga.domain.repository.AlojamientoRepository;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.Reporte;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

/** Calcula ingresos, reservas y ocupación global para el intervalo solicitado. */
@Service
public class ConsultarIndicadoresConsolidadosUseCase {
    public record Command(LocalDate desde, LocalDate hasta) { }

    private static final Set<EstadoReserva> ACTIVAS = Set.of(
            EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO);

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final Clock clock;

    public ConsultarIndicadoresConsolidadosUseCase(AlojamientoRepository alojamientoRepository,
            ReservaRepository reservaRepository, FolioRepository folioRepository, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.clock = clock;
    }

    public Reporte ejecutar(Command command) {
        RangoFechas periodo = new RangoFechas(command.desde(), command.hasta());
        LocalDateTime inicio = command.desde().atStartOfDay();
        LocalDateTime fin = command.hasta().atStartOfDay();
        BigDecimal ingresos = folioRepository.buscarConPagosEntre(inicio, fin).stream()
                .flatMap(f -> f.getPagos().stream())
                .filter(p -> !p.fechaRegistro().isBefore(inicio) && p.fechaRegistro().isBefore(fin))
                .map(p -> p.monto().valor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        var reservas = reservaRepository.buscarQueSolapanCon(periodo);
        long total = reservas.size();
        long activas = reservas.stream().filter(r -> ACTIVAS.contains(r.getEstado())).count();
        long canceladas = reservas.stream().filter(r -> r.getEstado() == EstadoReserva.CANCELADA).count();
        long nochesVendidas = reservas.stream().filter(r -> ACTIVAS.contains(r.getEstado())
                        || r.getEstado() == EstadoReserva.FINALIZADA)
                .mapToLong(r -> {
                    LocalDate desde = r.getEstancia().fechaEntrada().isAfter(command.desde())
                            ? r.getEstancia().fechaEntrada() : command.desde();
                    LocalDate hasta = r.getEstancia().fechaSalida().isBefore(command.hasta())
                            ? r.getEstancia().fechaSalida() : command.hasta();
                    return Math.max(0, ChronoUnit.DAYS.between(desde, hasta));
                }).sum();
        long nochesPeriodo = ChronoUnit.DAYS.between(command.desde(), command.hasta());
        long nochesDisponibles = Cargas.alojamiento(alojamientoRepository).apartamentosActivos().size() * nochesPeriodo;
        BigDecimal ocupacion = nochesDisponibles == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(nochesVendidas).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(nochesDisponibles), 2, java.math.RoundingMode.HALF_UP);

        return new Reporte(periodo, Map.of(
                "ingresosTotales", ingresos,
                "ocupacionGlobal", ocupacion,
                "reservasTotales", BigDecimal.valueOf(total),
                "reservasActivas", BigDecimal.valueOf(activas),
                "reservasCanceladas", BigDecimal.valueOf(canceladas)), LocalDateTime.now(clock));
    }
}
