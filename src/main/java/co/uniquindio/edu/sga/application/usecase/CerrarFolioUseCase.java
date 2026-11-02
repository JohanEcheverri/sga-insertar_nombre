package co.uniquindio.edu.sga.application.usecase;

import org.springframework.transaction.annotation.Transactional;

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
 * PUT /api/reservas/{codigo}/folio/cerrar — RN-17: con saldo pendiente solo cierra el ADMINISTRADOR,
 * dejando registrado autor y motivo. Con saldo en cero cierra recepción o administración.
 */
@Service
@Transactional
public class CerrarFolioUseCase {

    public record Command(UUID reservaId, Solicitante solicitante, String motivo) { }

    private final FolioRepository folioRepository;
    private final Clock clock;

    public CerrarFolioUseCase(FolioRepository folioRepository, Clock clock) {
        this.folioRepository = folioRepository;
        this.clock = clock;
    }

    public FolioDetalle ejecutar(Command cmd) {
        if (!cmd.solicitante().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo recepción o administración pueden cerrar un folio.");
        }
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        LocalDateTime ahora = LocalDateTime.now(clock);
        String autor = cmd.solicitante().nombre() == null ? "sistema" : cmd.solicitante().nombre();

        if (folio.tieneSaldoPendiente()) {
            cmd.solicitante().exigirAdministrador();
            if (cmd.motivo() == null || cmd.motivo().isBlank()) {
                throw new ReglaDominioException("RN-17 - Cerrar con saldo pendiente exige registrar el motivo.");
            }
            folio.cerrarConSaldoAutorizado(new AutorizacionCierre(autor, cmd.motivo(), ahora));
        } else {
            String motivo = cmd.motivo() == null || cmd.motivo().isBlank() ? "Cierre ordinario" : cmd.motivo();
            folio.cerrar(new AutorizacionCierre(autor, motivo, ahora));
        }
        folioRepository.guardar(folio);
        return FolioDetalle.de(folio);
    }
}
