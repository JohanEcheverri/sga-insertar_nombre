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
import java.time.*;
import java.util.*;

/**
 * PUT /api/politicas-cancelacion — Crea una NUEVA versión de la política (v1 → v2 …). Las reservas
 * existentes conservan la versión congelada (RN-13, RN-22). Sirve también para definir la inicial.
 */
@Service
@Transactional
public class ActualizarPoliticaCancelacionUseCase {

    public record Tramo(int diasAntelacionMinimos, BigDecimal porcentajeRetencion) { }

    public record Command(List<Tramo> tramos, BigDecimal porcentajeRetencionNoShow, Solicitante solicitante) { }

    public record Result(String versionAnterior, String versionNueva) { }

    private final AlojamientoRepository alojamientoRepository;

    public ActualizarPoliticaCancelacionUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public Result ejecutar(Command cmd) {
        if (cmd.solicitante() == null) throw new AccesoDenegadoException("Debe autenticarse para modificar la política.");
        cmd.solicitante().exigirAdministrador();
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        String anterior = alojamiento.getPoliticaVigente().version().valor();
        String nueva = "v" + (numero(anterior) + 1);

        List<TramoCancelacion> tramos = new ArrayList<>();
        for (Tramo t : cmd.tramos()) tramos.add(new TramoCancelacion(t.diasAntelacionMinimos(), t.porcentajeRetencion()));
        // L-10: al menos 2 tramos de antelación
        if (tramos.size() < 2) {
            throw new ReglaDominioException("La política debe tener al menos dos tramos de antelación.");
        }
        alojamiento.cambiarPoliticaVigente(
                new PoliticaCancelacion(new VersionPolitica(nueva, LocalDate.now()), tramos, cmd.porcentajeRetencionNoShow()));
        alojamientoRepository.guardar(alojamiento);
        return new Result(anterior, nueva);
    }

    private static int numero(String version) {
        try {
            return Integer.parseInt(version.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
