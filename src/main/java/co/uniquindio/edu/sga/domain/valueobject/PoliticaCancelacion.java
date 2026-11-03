package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Política inmutable que define retenciones por antelación y para no-show. */
public record PoliticaCancelacion(VersionPolitica version,
                                  List<TramoCancelacion> tramos,
                                  BigDecimal porcentajeRetencionNoShow) {
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
        if (porcentajeRetencionNoShow == null || porcentajeRetencionNoShow.signum() < 0 || porcentajeRetencionNoShow.compareTo(BigDecimal.ONE) > 0) {
            throw new ReglaDominioException("El porcentaje de retención por no-show debe estar entre 0 y 1.");
        }

        List<TramoCancelacion> tramosOrdenados = tramos.stream()
                .sorted(Comparator.comparingInt(TramoCancelacion::diasAntelacionMinimos))
                .toList();
        if (tramosOrdenados.getFirst().diasAntelacionMinimos() != 0) {
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

    /**
     * Calcula la retención por cancelación según las horas de antelación y el valor base de la reserva.
     */
    public Dinero calcularRetencionCancelacion(long horasAntelacion, Dinero valorBase) {
        // si cancela con más de 48 horas de anticipación, la retención es del 10%, de lo contrario es del 50%.
        BigDecimal porcentajeRetencion = (horasAntelacion >= 48) ? new BigDecimal("0.10") : new BigDecimal("0.50");
        return valorBase.multiplicar(porcentajeRetencion);
    }

    /**
     * Calcula la retención por no-show aplicando las reglas de la política al valor base.
     */
    public Dinero calcularRetencionNoShow(Dinero valorBase) {
        //él no-show aplica una retención del 100%.
        return valorBase.multiplicar(BigDecimal.ONE);
    }

    /** RN-13 - La retención de cancelación se determina según la antelación con que se cancela. */
    public BigDecimal retencionParaDias(long diasAntelacion) {
        if (diasAntelacion < 0) {
            throw new ReglaDominioException("Los días de antelación no pueden ser negativos.");
        }
        TramoCancelacion aplicable = tramos.getFirst();
        for (TramoCancelacion tramo : tramos) {
            if (tramo.diasAntelacionMinimos() > diasAntelacion) break;
            aplicable = tramo;
        }
        return aplicable.porcentajeRetencion();
    }

    /** RN-13 - La retención aplicable a un no-show se toma de la configuración específica de la política. */
    public BigDecimal retencionPorNoShow() {
        return porcentajeRetencionNoShow;
    }
}
