package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
/** desde/hasta: ambos INCLUSIVOS (10 al 20 de julio); null en la temporada base. */
public record TemporadaDetalle(UUID id, String nombre, LocalDate desde, LocalDate hasta, boolean base,
                               List<TarifaDetalle> tarifas) {

    public record TarifaDetalle(UUID apartamentoId, double precioPorNochePorOcupante) { }

    public static TemporadaDetalle de(Temporada t) {
        return new TemporadaDetalle(t.getId().valor(), t.getNombre(),
                t.esBase() ? null : t.getRangoFechas().fechaInicio(),
                t.esBase() ? null : t.getRangoFechas().fechaFin().minusDays(1), t.esBase(),
                t.getTarifas().stream().map(x -> new TarifaDetalle(x.apartamentoId().valor(),
                        x.precioPorNochePorOcupante().valor())).toList());
    }
}
