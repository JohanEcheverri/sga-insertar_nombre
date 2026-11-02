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

/** GET /api/reservas/{codigo}/folio — Un huésped solo ve el folio de sus propias reservas. */
@Service
public class ConsultarFolioUseCase {

    public record Command(UUID reservaId, Solicitante solicitante) { }

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;

    public ConsultarFolioUseCase(ReservaRepository reservaRepository, FolioRepository folioRepository) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
    }

    public FolioDetalle ejecutar(Command cmd) {
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        cmd.solicitante().exigirAccesoA(reserva);
        return FolioDetalle.de(Cargas.folioDeReserva(folioRepository, cmd.reservaId()));
    }
}
