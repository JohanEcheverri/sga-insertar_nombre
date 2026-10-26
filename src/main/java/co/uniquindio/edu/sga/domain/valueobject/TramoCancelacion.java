package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

/** Umbral mínimo de antelación y porcentaje retenido al cancelar. */
public record TramoCancelacion(int diasAntelacionMinimos, double porcentajeRetencion) {
    public TramoCancelacion {
        // Regla del tramo: los días mínimos no pueden ser negativos y la retención debe estar entre 0 y 1.
        if (diasAntelacionMinimos < 0) {
            throw new ReglaDominioException("Los días mínimos de antelación no pueden ser negativos.");
        }
        if (!Double.isFinite(porcentajeRetencion)
                || porcentajeRetencion < 0 || porcentajeRetencion > 1) {
            throw new ReglaDominioException("El porcentaje de retención debe estar entre 0 y 1.");
        }
    }
}
