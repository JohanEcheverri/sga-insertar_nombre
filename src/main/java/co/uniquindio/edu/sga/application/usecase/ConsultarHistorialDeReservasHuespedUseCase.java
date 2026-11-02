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

/** GET /api/usuarios/{id}/reservas — Historial del huésped; solo el propio usuario o personal interno. */
@Service
public class ConsultarHistorialDeReservasHuespedUseCase {

    public record Command(UUID usuarioId, Solicitante solicitante, int pagina) { }

    private final ReservaRepository reservaRepository;

    public ConsultarHistorialDeReservasHuespedUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Pagina<ReservaDetalle> ejecutar(Command cmd) {
        Solicitante s = cmd.solicitante();
        boolean propio = s.usuarioId() != null && s.usuarioId().valor().equals(cmd.usuarioId());
        if (!propio && !s.esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo puede consultar su propio historial de reservas.");
        }
        FiltroReservas filtro = new FiltroReservas(null, null, null, null, UsuarioId.of(cmd.usuarioId()),
                null, null, null, null);
        return reservaRepository.buscar(filtro, cmd.pagina()).mapear(ReservaDetalle::de);
    }
}
