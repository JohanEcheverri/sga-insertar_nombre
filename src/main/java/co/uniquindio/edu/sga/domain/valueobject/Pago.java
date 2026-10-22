package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDateTime;

/** Registro inmutable de un pago aplicado al folio. */
public record Pago(Dinero monto, MedioPago medio, String referencia, LocalDateTime fechaRegistro) {
    public Pago {
        if (monto == null) throw new ReglaDominioException("El pago debe tener un monto válido.");
        if (medio == null) throw new ReglaDominioException("El pago debe especificar un medio de pago.");
        if (referencia != null) {
            referencia = referencia.trim();
            if (referencia.isEmpty()) referencia = null;
        }
        if (fechaRegistro == null) throw new ReglaDominioException("El pago debe registrar fecha y hora.");
    }
}
