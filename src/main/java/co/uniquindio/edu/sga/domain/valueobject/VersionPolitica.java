package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

/** Identificador legible e inmutable de una versión de política de cancelación. */
public record VersionPolitica(String valor) {
    public VersionPolitica {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("La versión de la política no puede estar vacía.");
        }
        valor = valor.trim();
    }
}
