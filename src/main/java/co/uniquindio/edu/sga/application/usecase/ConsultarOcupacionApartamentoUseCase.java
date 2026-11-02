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

/**
 * GET /api/reportes/ocupacion — 7.9: noches vendidas y % de ocupación = noches vendidas /
 * (apartamentos activos × noches del periodo). Periodo [desde, hasta). Cuentan reservas PENDIENTE,
 * CONFIRMADA, EN_CURSO y FINALIZADA; las canceladas y no-show no ocuparon noches.
 */
@Service
public class ConsultarOcupacionApartamentoUseCase {

    public record Command(LocalDate desde, LocalDate hasta) { }

    public record Result(long nochesVendidas, long nochesDisponibles, double porcentajeOcupacion) { }

    private static final Set<EstadoReserva> VENDEN = Set.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA,
            EstadoReserva.EN_CURSO, EstadoReserva.FINALIZADA);

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;

    public ConsultarOcupacionApartamentoUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
    }

    public Result ejecutar(Command cmd) {
        RangoFechas periodo = new RangoFechas(cmd.desde(), cmd.hasta());
        int activos = Cargas.alojamiento(alojamientoRepository).apartamentosActivos().size();
        long vendidas = 0;
        for (Reserva r : reservaRepository.buscarQueSolapanCon(periodo)) {
            if (!VENDEN.contains(r.getEstado())) continue;
            LocalDate ini = r.getEstancia().fechaEntrada().isAfter(cmd.desde()) ? r.getEstancia().fechaEntrada() : cmd.desde();
            LocalDate fin = r.getEstancia().fechaSalida().isBefore(cmd.hasta()) ? r.getEstancia().fechaSalida() : cmd.hasta();
            vendidas += Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(ini, fin));
        }
        long noches = java.time.temporal.ChronoUnit.DAYS.between(cmd.desde(), cmd.hasta());
        long disponibles = activos * noches;
        double pct = disponibles == 0 ? 0 : (double) vendidas / disponibles * 100.0;
        return new Result(vendidas, disponibles, pct);
    }
}
