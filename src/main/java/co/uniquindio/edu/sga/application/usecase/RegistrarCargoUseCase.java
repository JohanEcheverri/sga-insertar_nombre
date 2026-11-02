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
 * POST /api/reservas/{codigo}/folio/cargos — Cargos manuales: servicio adicional, ajuste o ajuste a favor
 * (movimiento inverso, RN-16). El alojamiento, los reversos y las penalidades los genera el sistema.
 */
@Service
@Transactional
public class RegistrarCargoUseCase {

    private static final Set<TipoCargo> MANUALES =
            Set.of(TipoCargo.SERVICIO_ADICIONAL, TipoCargo.AJUSTE_MODIFICACION, TipoCargo.AJUSTE_A_FAVOR);

    public record Command(UUID reservaId, TipoCargo tipo, String concepto, BigDecimal monto, Solicitante solicitante) { }

    private final FolioRepository folioRepository;
    private final ReservaRepository reservaRepository;
    private final Clock clock;

    public RegistrarCargoUseCase(FolioRepository folioRepository, ReservaRepository reservaRepository, Clock clock) {
        this.folioRepository = folioRepository;
        this.reservaRepository = reservaRepository;
        this.clock = clock;
    }

    public FolioDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null || !cmd.solicitante().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo personal interno puede registrar cargos manuales.");
        }
        Cargas.reserva(reservaRepository, cmd.reservaId());
        if (!MANUALES.contains(cmd.tipo())) {
            throw new OperacionNoPermitidaException("El cargo de tipo " + cmd.tipo()
                    + " lo genera el sistema; solo se registran manualmente: " + MANUALES);
        }
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        folio.registrarCargo(new Cargo(cmd.tipo(), cmd.concepto(), Dinero.de(cmd.monto()), LocalDateTime.now(clock)));
        folioRepository.guardar(folio);
        return FolioDetalle.de(folio);
    }
}
