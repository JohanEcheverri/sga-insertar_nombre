package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

/** Identificador tipado de usuario usado por el dominio. */
public record UsuarioId (UUID valor){
    
    public UsuarioId {
        if (valor == null) {
            throw new ReglaDominioException("El identificador del usuario no puede ser nulo");
        }
    }

    public static UsuarioId of(UUID valor) {
        return new UsuarioId(valor);
    }

    public static UsuarioId generar() {
        return new UsuarioId(UUID.randomUUID());
    }
}

