package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record IdNovedad(String valor) {

    public IdNovedad {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador de la novedad es obligatorio.");
        }
        valor = valor.trim();
    }
}