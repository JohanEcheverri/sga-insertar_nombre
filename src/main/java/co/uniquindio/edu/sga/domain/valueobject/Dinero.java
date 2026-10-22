package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record Dinero(double valor) {

    public Dinero{
        // Regla del valor Dinero: todo monto debe ser positivo, finito y representable.
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new ReglaDominioException("El valor debe ser un monto positivo y finito.");
        }
    }
    
}
