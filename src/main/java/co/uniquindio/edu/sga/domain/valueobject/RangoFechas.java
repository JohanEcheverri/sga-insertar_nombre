package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDate;

/** Intervalo de fechas [fechaInicio, fechaFin): incluye inicio y excluye fin. */
public record RangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
    public RangoFechas {
        if (fechaInicio == null || fechaFin == null) {
            throw new ReglaDominioException("El rango debe tener fecha de inicio y fecha de fin.");
        }
        // Regla del rango: la fecha final debe ser posterior a la inicial; el intervalo contiene al menos una noche/día.
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new ReglaDominioException("La fecha final del rango debe ser posterior a la fecha inicial.");
        }
    }

    /** Indica si este rango cubre completamente al rango consultado. */
    public boolean cubre(RangoFechas rango) {
        exigirRango(rango);
        return !fechaInicio.isAfter(rango.fechaInicio) && !fechaFin.isBefore(rango.fechaFin);
    }

    /** Dos rangos semiabiertos intersectan si comparten al menos una fecha/noche. */
    public boolean intersecta(RangoFechas rango) {
        exigirRango(rango);
        return fechaInicio.isBefore(rango.fechaFin) && rango.fechaInicio.isBefore(fechaFin);
    }

    private static void exigirRango(RangoFechas rango) {
        if (rango == null) throw new ReglaDominioException("El rango consultado no puede ser nulo.");
    }
}
