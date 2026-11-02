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

/** GET /api/apartamentos/{id}/bloqueos — Bloqueos administrativos del apartamento (paginados). */
public class ConsultarBloqueoUseCase {

    public record Command(UUID apartamentoId, int pagina) { }

    private final AlojamientoRepository alojamientoRepository;
    private final BloqueoRepository bloqueoRepository;

    public ConsultarBloqueoUseCase(AlojamientoRepository alojamientoRepository, BloqueoRepository bloqueoRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    public Pagina<BloqueoDetalle> ejecutar(Command cmd) {
        Apartamento apartamento = Cargas.apartamento(Cargas.alojamiento(alojamientoRepository), cmd.apartamentoId());
        return Pagina.de(bloqueoRepository.buscarPorApartamento(apartamento.getId()).stream()
                .map(BloqueoDetalle::de).toList(), cmd.pagina());
    }
}
