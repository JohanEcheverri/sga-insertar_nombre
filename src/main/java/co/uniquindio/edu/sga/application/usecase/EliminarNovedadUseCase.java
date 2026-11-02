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

/** DELETE /api/novedades/{id} — Anulación (borrado lógico) de un reporte. */
public class EliminarNovedadUseCase {

    public record Command(UUID novedadId) { }

    private final NovedadRepository novedadRepository;

    public EliminarNovedadUseCase(NovedadRepository novedadRepository) {
        this.novedadRepository = novedadRepository;
    }

    public void ejecutar(Command cmd) {
        Novedad novedad = novedadRepository.buscarPorId(NovedadId.of(cmd.novedadId()))
                .orElseThrow(() -> new NovedadNoEncontradaException(String.valueOf(cmd.novedadId())));
        novedad.anular();
        novedadRepository.guardar(novedad);
    }
}
