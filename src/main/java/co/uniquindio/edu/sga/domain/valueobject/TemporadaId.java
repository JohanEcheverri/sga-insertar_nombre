package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record TemporadaId(UUID valor) {

    public TemporadaId {
        if (valor == null) {
            throw new ReglaDominioException("El valor de la temporada no puede ser nulo");
        }
    }

    public static TemporadaId of(UUID valor) {
        return new TemporadaId(valor);
    }

    public static TemporadaId generar() {
        return new TemporadaId(UUID.randomUUID());
    }

}
