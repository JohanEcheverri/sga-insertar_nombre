package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public boolean incluye(LocalDate noche) {
        return !noche.isBefore(fechaEntrada) && noche.isBefore(fechaSalida);
    }

    // RN-05: sirve para liquidar noche por noche, aplicando la tarifa vigente en cada una
    public List<LocalDate> nochesOcupadas() {
        List<LocalDate> resultado = new ArrayList<>();
        for (LocalDate noche = fechaEntrada; noche.isBefore(fechaSalida); noche = noche.plusDays(1)) {
            resultado.add(noche);
        }
        return List.copyOf(resultado);
    }

    /** RN-01: un apartamento no puede tener dos reservas activas solapadas. */
    public boolean seSolapaCon(Estancia otra) {
        return fechaEntrada.isBefore(otra.fechaSalida) && otra.fechaEntrada.isBefore(fechaSalida);
    }

    /** Convierte la estancia al intervalo semiabierto usado para comparar ocupación y bloqueos. */
    public RangoFechas rangoFechas() {
        return new RangoFechas(fechaEntrada, fechaSalida);
    }
}
