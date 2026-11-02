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

/** PUT /api/temporadas/{id} — Cambia nombre y fechas (inclusivas) sin solaparse (RN-27); conserva sus tarifas. La base no se modifica. */
@Service
@Transactional
public class ModificarTemporadaUseCase {

    public record Command(UUID temporadaId, String nombre, LocalDate desde, LocalDate hasta, Solicitante solicitante) { }

    private final AlojamientoRepository alojamientoRepository;

    public ModificarTemporadaUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public TemporadaDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null) throw new AccesoDenegadoException("Debe autenticarse para modificar una temporada.");
        cmd.solicitante().exigirAdministrador();
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Temporada actual = Cargas.temporada(alojamiento, cmd.temporadaId());
        Temporada nueva = Temporada.crear(actual.getId(), cmd.nombre(),
                new RangoFechas(cmd.desde(), cmd.hasta().plusDays(1)), new ArrayList<>(actual.getTarifas()));
        alojamiento.reemplazarTemporada(nueva);
        alojamientoRepository.guardar(alojamiento);
        return TemporadaDetalle.de(nueva);
    }
}
