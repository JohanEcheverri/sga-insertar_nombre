package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Capacidad(byte aforoMax) {

    public Capacidad{
        // Regla de capacidad: el aforo máximo del apartamento debe ser mayor que cero.
        if (aforoMax <= 0) {
            throw new ReglaDominioException("El aforo máximo debe ser mayor a 0.");
        }
    }
}
