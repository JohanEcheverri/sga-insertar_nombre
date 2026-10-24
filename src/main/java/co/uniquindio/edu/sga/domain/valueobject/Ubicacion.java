package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Ubicacion(String departamento, String ciudad, String direccion) {

    public Ubicacion {
        if (departamento == null || departamento.isBlank()) {
            throw new ReglaDominioException("El departamento es obligatorio.");
        }
        if (ciudad == null || ciudad.isBlank()) {
            throw new ReglaDominioException("La ciudad es obligatoria.");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaDominioException("La dirección es obligatoria.");
        }
    }
}