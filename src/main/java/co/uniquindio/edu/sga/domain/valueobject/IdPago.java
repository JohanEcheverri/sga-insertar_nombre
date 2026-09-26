package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record IdPago(String valor) {

    public IdPago {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El identificador del pago es obligatorio.");
        }
        valor = valor.trim();
    }
}
