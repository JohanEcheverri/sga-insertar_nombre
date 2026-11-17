package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

/** Umbral mínimo de antelación en horas y porcentaje retenido al cancelar. */
public record TramoCancelacion(int horasAntelacionMinimas, BigDecimal porcentajeRetencion) {
    public TramoCancelacion {
        if (horasAntelacionMinimas < 0) {
            throw new ReglaDominioException("Las horas mínimas de antelación no pueden ser negativas.");
        }
        if (porcentajeRetencion == null || porcentajeRetencion.signum() < 0 || porcentajeRetencion.compareTo(BigDecimal.ONE) > 0) {
            throw new ReglaDominioException("El porcentaje de retención debe estar entre 0 y 1.");
        }
    }

    /** Compatibilidad con reservas históricas configuradas cuando los umbrales se expresaban en días. */
    public int diasAntelacionMinimos() { return horasAntelacionMinimas; }
}
