package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record ApartamentoId(UUID valor) {

    public ApartamentoId {
        if (valor == null) {
            throw new ReglaDominioException("El valor del apartamento no puede ser nulo");
        }
    }

    public static ApartamentoId of(UUID valor) {
        return new ApartamentoId(valor);
    }

    public static ApartamentoId generar() {
        return new ApartamentoId(UUID.randomUUID());
    }
}
