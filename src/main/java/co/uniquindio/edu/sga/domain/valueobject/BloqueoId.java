package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record BloqueoId(UUID valor) {

    public BloqueoId {
        if (valor == null) {
            throw new ReglaDominioException("El valor del bloqueo no puede ser nulo");
        }
    }

    public static BloqueoId of(UUID valor) {
        return new BloqueoId(valor);
    }

    public static BloqueoId generar() {
        return new BloqueoId(UUID.randomUUID());
    }
}
