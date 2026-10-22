package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record UsuarioId (UUID valor){
    
    public UsuarioId {
        if (valor == null) {
            throw new ReglaDominioException("El valor del usuario no puede ser nulo");
        }
    }

    public static UsuarioId of(UUID valor) {
        return new UsuarioId(valor);
    }

    public static UsuarioId generar() {
        return new UsuarioId(UUID.randomUUID());
    }
}

