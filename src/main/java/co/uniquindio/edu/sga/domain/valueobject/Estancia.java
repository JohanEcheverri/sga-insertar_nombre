package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDate;

public record Estancia(LocalDate fechaEntrada, LocalDate fechaSalida) {
    public Estancia {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new ReglaDominioException("La estancia debe tener fechas de entrada y salida.");
        }
        // Regla de estancia: la fecha de salida debe ser posterior a la fecha de entrada, garantizando al menos una noche.
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new ReglaDominioException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
    }
}
