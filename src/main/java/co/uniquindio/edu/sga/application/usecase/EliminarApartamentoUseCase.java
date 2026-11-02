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

/** DELETE /api/apartamentos/{id} — Borrado lógico; solo si no tiene reservas activas ni futuras (7.3). */
public class EliminarApartamentoUseCase {

    public record Command(UUID apartamentoId) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final Clock clock;

    public EliminarApartamentoUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.clock = clock;
    }

    public void ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Apartamento apartamento = Cargas.apartamento(alojamiento, cmd.apartamentoId());
        if (reservaRepository.existeActivaOFuturaDeApartamento(apartamento.getId(), LocalDate.now(clock))) {
            throw new OperacionNoPermitidaException(
                    "No se puede retirar el apartamento: tiene reservas activas o futuras.");
        }
        apartamento.retirar();
        alojamientoRepository.guardar(alojamiento);
    }
}
