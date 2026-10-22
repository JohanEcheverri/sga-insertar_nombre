package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record NovedadId(UUID valor) {

    public NovedadId {
        if (valor == null) {
            throw new ReglaDominioException("El valor de la Novedad no puede ser nulo");
        }
    }

    public static NovedadId of(UUID valor) {
        return new NovedadId(valor);
    }

    public static NovedadId generar() {
        return new NovedadId(UUID.randomUUID());
    }
}
