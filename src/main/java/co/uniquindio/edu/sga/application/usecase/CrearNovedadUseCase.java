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

/** POST /api/apartamentos/{id}/novedades — RN-31: reportar NO cambia el estado operativo (eso es decisión administrativa). */
@Service
@Transactional
public class CrearNovedadUseCase {

    public record Command(UUID apartamentoId, String autor, String descripcion, GravedadNovedad gravedad) { }

    private final AlojamientoRepository alojamientoRepository;
    private final NovedadRepository novedadRepository;
    private final Clock clock;

    public CrearNovedadUseCase(AlojamientoRepository alojamientoRepository, NovedadRepository novedadRepository, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.novedadRepository = novedadRepository;
        this.clock = clock;
    }

    public NovedadDetalle ejecutar(Command cmd) {
        Apartamento apartamento = Cargas.apartamento(Cargas.alojamiento(alojamientoRepository), cmd.apartamentoId());
        Novedad novedad = Novedad.reportar(apartamento.getId(), cmd.autor(), cmd.descripcion(), cmd.gravedad(),
                LocalDateTime.now(clock));
        novedadRepository.guardar(novedad);
        return NovedadDetalle.de(novedad);
    }
}
