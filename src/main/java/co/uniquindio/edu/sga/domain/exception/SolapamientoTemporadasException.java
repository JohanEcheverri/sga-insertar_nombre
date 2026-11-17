package co.uniquindio.edu.sga.domain.exception;

/** Indica que dos temporadas tarifarias se solapan. */
public class SolapamientoTemporadasException extends DomainException {

    public SolapamientoTemporadasException(String message) {
        super(message);
    }

    public SolapamientoTemporadasException(String message, Throwable cause) {
        super(message, cause);
    }
}