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

/**
 * POST /api/temporadas — RN-27: no se solapan con otras; cada temporada tiene tarifa para cada apartamento.
 * desde y hasta son INCLUSIVOS (lenguaje del negocio: "del 10 al 20 de julio"). Si no se envían tarifas,
 * se copian las de la temporada base para que el administrador las ajuste luego (AsignarTarifaUseCase).
 */
public class CrearTemporadaUseCase {

    public record Command(String nombre, LocalDate desde, LocalDate hasta, Map<UUID, Double> tarifasPorApartamento) { }

    private final AlojamientoRepository alojamientoRepository;

    public CrearTemporadaUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public TemporadaDetalle ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        TemporadaId id = TemporadaId.generar();
        List<Tarifa> tarifas = new ArrayList<>();

        if (cmd.tarifasPorApartamento() == null || cmd.tarifasPorApartamento().isEmpty()) {
            Temporada base = alojamiento.getTemporadas().stream().filter(Temporada::esBase).findFirst()
                    .orElseThrow(() -> new ReglaDominioException("F-05 - El alojamiento debe tener temporada base."));
            for (Tarifa t : base.getTarifas()) {
                tarifas.add(new Tarifa(id, t.apartamentoId(), t.precioPorNochePorOcupante()));
            }
        } else {
            cmd.tarifasPorApartamento().forEach((apto, precio) ->
                    tarifas.add(new Tarifa(id, ApartamentoId.of(apto), new Dinero(precio))));
        }
        Temporada temporada = Temporada.crear(id, cmd.nombre(),
                new RangoFechas(cmd.desde(), cmd.hasta().plusDays(1)), tarifas);

        alojamiento.agregarTemporada(temporada);
        alojamientoRepository.guardar(alojamiento);
        return TemporadaDetalle.de(temporada);
    }
}
