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

/** POST /api/apartamentos/{id}/bloqueos — Decisión administrativa; RN-28 (sin reservas activas) y RN-24 (no toca el estado operativo). */
@Service
@Transactional
public class CrearBloqueoUseCase {

    /** hasta es EXCLUSIVO, igual que la fecha de salida de una estancia (3.1). */
    public record Command(UUID apartamentoId, LocalDate desde, LocalDate hasta, String motivo, Solicitante solicitante) { }

    private final AlojamientoRepository alojamientoRepository;
    private final BloqueoRepository bloqueoRepository;
    private final GestionBloqueosMantenimientoService bloqueosService;

    public CrearBloqueoUseCase(AlojamientoRepository alojamientoRepository, BloqueoRepository bloqueoRepository,
                               GestionBloqueosMantenimientoService bloqueosService) {
        this.alojamientoRepository = alojamientoRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.bloqueosService = bloqueosService;
    }

    public BloqueoDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null) throw new AccesoDenegadoException("Debe autenticarse para bloquear un apartamento.");
        cmd.solicitante().exigirAdministrador();
        Apartamento apartamento = Cargas.apartamento(Cargas.alojamiento(alojamientoRepository), cmd.apartamentoId());
        Bloqueo bloqueo = bloqueosService.crearBloqueo(apartamento, new RangoFechas(cmd.desde(), cmd.hasta()), cmd.motivo());
        bloqueoRepository.guardar(bloqueo);
        return BloqueoDetalle.de(bloqueo);
    }
}
