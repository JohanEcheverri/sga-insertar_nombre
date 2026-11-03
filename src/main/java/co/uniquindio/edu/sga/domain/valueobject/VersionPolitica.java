package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

/** Identificador legible e inmutable de una versión de política de cancelación. */
public record VersionPolitica(String valor, LocalDate vigenteDesde) {
    public VersionPolitica {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("La versión de la política no puede estar vacía.");
        }
        if (vigenteDesde == null) {
            throw new ReglaDominioException("La versión debe tener una fecha de vigencia.");
        }
        valor = valor.trim();
    }
}
