package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record IdBloqueo(String valor) {

    public IdBloqueo {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador del bloqueo es obligatorio.");
        }
        valor = valor.trim();
    }
}
