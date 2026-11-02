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

/** POST /api/reservas/{codigo}/folio/pagos — RN-15: todo pago lleva medio y fecha. Admite pagos parciales y medios distintos. */
public class RegistrarPagoUseCase {

    public record Command(UUID reservaId, MedioPago medio, BigDecimal monto, String referencia) { }

    private final FolioRepository folioRepository;
    private final Clock clock;

    public RegistrarPagoUseCase(FolioRepository folioRepository, Clock clock) {
        this.folioRepository = folioRepository;
        this.clock = clock;
    }

    public FolioDetalle ejecutar(Command cmd) {
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        folio.registrarPago(new Pago(Dinero.de(cmd.monto()), cmd.medio(), cmd.referencia(), LocalDateTime.now(clock)));
        folioRepository.guardar(folio);
        return FolioDetalle.de(folio);
    }
}
