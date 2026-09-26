package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

// L-04: la identificación de cada apartamento es única y estable
public record IdentificacionApartamento(String valor) {

    public IdentificacionApartamento {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("La identificación del apartamento es obligatoria.");
        }
        valor = valor.trim().toUpperCase();
    }
}