package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;

public record AlojamientoId(UUID valor) {
public AlojamientoId {
        if (valor == null) {
            throw new ReglaDominioException("El valor de la reserva no puede ser nulo");
        }
    }

    public static AlojamientoId of(UUID valor) {
        return new AlojamientoId(valor);
    }

    public static AlojamientoId generar() {
        return new AlojamientoId(UUID.randomUUID());
    }
}

