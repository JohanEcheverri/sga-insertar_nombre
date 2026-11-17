package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.time.Duration;

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
                .sorted(Comparator.comparingInt(TramoCancelacion::horasAntelacionMinimas))
                .toList();
        if (tramosOrdenados.getFirst().horasAntelacionMinimas() != 0) {
            throw new ReglaDominioException("La política debe incluir un tramo desde cero horas de antelación.");
        }
        for (int i = 1; i < tramosOrdenados.size(); i++) {
            if (tramosOrdenados.get(i - 1).horasAntelacionMinimas()
                    == tramosOrdenados.get(i).horasAntelacionMinimas()) {
                throw new ReglaDominioException("No puede haber tramos con el mismo mínimo de horas de antelación.");
            }
        }
        tramos = List.copyOf(tramosOrdenados);
    }

    /**
     * Calcula la retención por cancelación según las horas de antelación y el valor base de la reserva.
     */
    public Dinero calcularRetencionCancelacion(long horasAntelacion, Dinero valorBase) {
        return valorBase.multiplicar(retencionParaHoras(Duration.ofHours(Math.max(0, horasAntelacion))));
    }

    /**
     * Calcula la retención por no-show aplicando las reglas de la política al valor base.
     */
    public Dinero calcularRetencionNoShow(Dinero valorBase) {
        return valorBase.multiplicar(porcentajeRetencionNoShow);
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

    /** Retención para una diferencia exacta hasta check-in; el límite superior del último tramo es estricto. */
    public BigDecimal retencionParaHoras(Duration antelacion) {
        return tramos.get(tramoIndexParaHoras(antelacion)).porcentajeRetencion();
    }

    public int tramoIndexParaHoras(Duration antelacion) {
        if (antelacion == null || antelacion.isNegative()) throw new ReglaDominioException("La antelación debe ser válida.");
        int aplicable = 0;
        for (int i = 1; i < tramos.size(); i++) {
            Duration umbral = Duration.ofHours(tramos.get(i).horasAntelacionMinimas());
            boolean ultimo = i == tramos.size() - 1;
            if (ultimo ? antelacion.compareTo(umbral) > 0 : antelacion.compareTo(umbral) >= 0) aplicable = i;
            else break;
        }
        return aplicable;
    }

    /** RN-13 - La retención aplicable a un no-show se toma de la configuración específica de la política. */
    public BigDecimal retencionPorNoShow() {
        return porcentajeRetencionNoShow;
    }
}
