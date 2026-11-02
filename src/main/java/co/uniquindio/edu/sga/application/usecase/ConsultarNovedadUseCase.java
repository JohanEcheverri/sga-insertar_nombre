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

/** GET /api/apartamentos/{id}/novedades — Historial de novedades (sin las anuladas), más recientes primero. */
@Service
public class ConsultarNovedadUseCase {

    public record Command(UUID apartamentoId, int pagina) { }

    private final AlojamientoRepository alojamientoRepository;
    private final NovedadRepository novedadRepository;

    public ConsultarNovedadUseCase(AlojamientoRepository alojamientoRepository, NovedadRepository novedadRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.novedadRepository = novedadRepository;
    }

    public Pagina<NovedadDetalle> ejecutar(Command cmd) {
        Apartamento apartamento = Cargas.apartamento(Cargas.alojamiento(alojamientoRepository), cmd.apartamentoId());
        return Pagina.de(novedadRepository.buscarPorApartamento(apartamento.getId()).stream()
                .filter(n -> !n.isAnulada())
                .sorted(Comparator.comparing(Novedad::getFechaReporte).reversed())
                .map(NovedadDetalle::de).toList(), cmd.pagina());
    }
}
