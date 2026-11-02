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

/** GET /api/reservas/{codigo} — Un huésped solo ve sus propias reservas (verificación por propietario, 7.2). */
public class ConsultarReservaPorCodigoUseCase {

    public record Command(UUID reservaId, Solicitante solicitante) { }

    private final ReservaRepository reservaRepository;

    public ConsultarReservaPorCodigoUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public ReservaDetalle ejecutar(Command cmd) {
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        cmd.solicitante().exigirAccesoA(reserva);
        return ReservaDetalle.de(reserva);
    }
}
