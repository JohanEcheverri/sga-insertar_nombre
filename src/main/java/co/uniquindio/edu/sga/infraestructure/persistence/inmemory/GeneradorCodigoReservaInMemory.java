package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.repository.GeneradorCodigoReserva;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Component
public class GeneradorCodigoReservaInMemory implements GeneradorCodigoReserva {

    @Override
    public ReservaId generar() {
        return new ReservaId(UUID.randomUUID());
    }
}