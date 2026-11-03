package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

/** Umbral mínimo de antelación y porcentaje retenido al cancelar. */
public record TramoCancelacion(int diasAntelacionMinimos, BigDecimal porcentajeRetencion) {
    public TramoCancelacion {
        // Regla del tramo: los días mínimos no pueden ser negativos y la retención debe estar entre 0 y 1.
        if (diasAntelacionMinimos < 0) {
            throw new ReglaDominioException("Los días mínimos de antelación no pueden ser negativos.");
        }
        if (porcentajeRetencion == null || porcentajeRetencion.signum() < 0 || porcentajeRetencion.compareTo(BigDecimal.ONE) > 0) {
            throw new ReglaDominioException("El porcentaje de retención debe estar entre 0 y 1.");
        }
    }
}
