package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.util.UUID;

public record FolioId(UUID valor) {
    
    public FolioId {
        if (valor == null) {
            throw new ReglaDominioException("El id del folio no puede ser nulo");
        }
    }

    public static FolioId of(UUID valor) {
        return new FolioId(valor);
    }

    public static FolioId generar() {
        return new FolioId(UUID.randomUUID());
    }

}
