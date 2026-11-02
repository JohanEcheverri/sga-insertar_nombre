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

/** POST /api/tarifas — Asigna o actualiza la tarifa de un apartamento en una temporada. Las reservas existentes no cambian (RN-22). */
public class AsignarTarifaUseCase {

    public record Command(UUID temporadaId, UUID apartamentoId, double precioPorNochePorOcupante) { }

    private final AlojamientoRepository alojamientoRepository;

    public AsignarTarifaUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public TemporadaDetalle ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Temporada temporada = Cargas.temporada(alojamiento, cmd.temporadaId());
        Cargas.apartamento(alojamiento, cmd.apartamentoId());
        alojamiento.establecerTarifa(new Tarifa(temporada.getId(), ApartamentoId.of(cmd.apartamentoId()),
                new Dinero(cmd.precioPorNochePorOcupante())));
        alojamientoRepository.guardar(alojamiento);
        return TemporadaDetalle.de(temporada);
    }
}
