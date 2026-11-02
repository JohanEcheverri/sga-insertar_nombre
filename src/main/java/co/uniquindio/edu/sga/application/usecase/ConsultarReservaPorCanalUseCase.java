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

/** GET /api/reservas/canales — Reservas por canal de origen, incluyendo canceladas y no-show (7.9). Reservas cuya estancia toca [desde, hasta). */
public class ConsultarReservaPorCanalUseCase {

    public record Command(LocalDate desde, LocalDate hasta) { }

    public record PorCanal(CanalOrigen canal, long total, long canceladas, long noShow) { }

    private final ReservaRepository reservaRepository;

    public ConsultarReservaPorCanalUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<PorCanal> ejecutar(Command cmd) {
        List<Reserva> reservas = reservaRepository.buscarQueSolapanCon(new RangoFechas(cmd.desde(), cmd.hasta()));
        List<PorCanal> resultado = new ArrayList<>();
        for (CanalOrigen canal : CanalOrigen.values()) {
            List<Reserva> delCanal = reservas.stream().filter(r -> r.getCanalOrigen() == canal).toList();
            resultado.add(new PorCanal(canal, delCanal.size(),
                    delCanal.stream().filter(r -> r.getEstado() == EstadoReserva.CANCELADA).count(),
                    delCanal.stream().filter(r -> r.getEstado() == EstadoReserva.NO_SHOW).count()));
        }
        return resultado;
    }
}
