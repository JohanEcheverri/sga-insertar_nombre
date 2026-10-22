package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import java.time.LocalDateTime;

/** Concepto de valor debitado al folio. */
public record Cargo(TipoCargo tipoCargo, String concepto, Dinero monto, LocalDateTime fechaRegistro) {
    public Cargo {
        // Regla del cargo: debe tener tipo, concepto no vacío, monto positivo y fecha de registro.
        if (tipoCargo == null) throw new ReglaDominioException("Se debe ingresar un tipo de cargo válido.");
        if (concepto == null || concepto.isBlank()) throw new ReglaDominioException("El concepto del cargo no puede estar vacío.");
        if (monto == null) throw new ReglaDominioException("El monto del cargo debe ser mayor a 0.");
        if (fechaRegistro == null) throw new ReglaDominioException("Se debe registrar la fecha y hora del cargo.");
        concepto = concepto.trim();
    }
}
