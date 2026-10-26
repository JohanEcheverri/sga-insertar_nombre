package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.util.Comparator;
import java.util.List;

/** Política inmutable que define retenciones por antelación y para no-show. */
public record PoliticaCancelacion(VersionPolitica version,
                                  List<TramoCancelacion> tramos,
                                  double porcentajeRetencionNoShow) {
    public PoliticaCancelacion {
        if (version == null) {
            throw new ReglaDominioException("La política de cancelación debe tener una versión.");
        }
        // Regla de política: debe existir al menos un tramo y cubrir cualquier antelación no negativa desde el día cero.
        if (tramos == null || tramos.isEmpty()) {
            throw new ReglaDominioException("La política debe contener al menos un tramo de cancelación.");
        }
        if (tramos.contains(null)) {
            throw new ReglaDominioException("La política no puede contener tramos nulos.");
        }
        if (!Double.isFinite(porcentajeRetencionNoShow)
                || porcentajeRetencionNoShow < 0 || porcentajeRetencionNoShow > 1) {
            throw new ReglaDominioException("El porcentaje de retención por no-show debe estar entre 0 y 1.");
        }

        List<TramoCancelacion> tramosOrdenados = tramos.stream()
                .sorted(Comparator.comparingInt(TramoCancelacion::diasAntelacionMinimos))
                .toList();
        if (tramosOrdenados.get(0).diasAntelacionMinimos() != 0) {
            throw new ReglaDominioException("La política debe incluir un tramo desde cero días de antelación.");
        }
        for (int i = 1; i < tramosOrdenados.size(); i++) {
            if (tramosOrdenados.get(i - 1).diasAntelacionMinimos()
                    == tramosOrdenados.get(i).diasAntelacionMinimos()) {
                throw new ReglaDominioException("No puede haber tramos con el mismo mínimo de días de antelación.");
            }
        }
        tramos = List.copyOf(tramosOrdenados);
    }

    /** RN-13 - La retención de cancelación se determina según la antelación con que se cancela. */
    public double retencionParaDias(long diasAntelacion) {
        if (diasAntelacion < 0) {
            throw new ReglaDominioException("Los días de antelación no pueden ser negativos.");
        }
        TramoCancelacion aplicable = tramos.get(0);
        for (TramoCancelacion tramo : tramos) {
            if (tramo.diasAntelacionMinimos() > diasAntelacion) break;
            aplicable = tramo;
        }
        return aplicable.porcentajeRetencion();
    }

    /** RN-13 - La retención aplicable a un no-show se toma de la configuración específica de la política. */
    public double retencionPorNoShow() {
        return porcentajeRetencionNoShow;
    }
}
