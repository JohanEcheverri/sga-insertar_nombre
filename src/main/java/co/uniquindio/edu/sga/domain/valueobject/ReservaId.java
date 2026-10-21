package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.util.UUID;

public record ReservaId(UUID valor) {
//! TODO ESTO FUE AUTOCOMPLETADO, ASI QUE NI IDEA :)
public ReservaId {
        if (valor == null) {
            throw new ReglaDominioException("El valor de la reserva no puede ser nulo");
        }
    }

    public static ReservaId of(UUID valor) {
        return new ReservaId(valor);
    }

    public static ReservaId generar() {
        return new ReservaId(UUID.randomUUID());
    }
}
