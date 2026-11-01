package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** Resultado de una consulta de indicadores consolidados o de ocupación. */
public record Reporte(
        RangoFechas periodo,
        Map<String, BigDecimal> indicadores,
        LocalDateTime fechaGeneracion
) {
    public Reporte {
        if (periodo == null) {
            throw new ReglaDominioException("El reporte debe indicar el período consultado.");
        }
        if (indicadores == null || indicadores.isEmpty()) {
            throw new ReglaDominioException("El reporte debe contener al menos un indicador.");
        }
        if (indicadores.entrySet().stream().anyMatch(e ->
                e.getKey() == null || e.getKey().isBlank() || e.getValue() == null)) {
            throw new ReglaDominioException("Los indicadores deben tener nombre y valor.");
        }
        if (fechaGeneracion == null) {
            throw new ReglaDominioException("El reporte debe indicar cuándo fue generado.");
        }
        indicadores = Map.copyOf(indicadores);
    }
}
