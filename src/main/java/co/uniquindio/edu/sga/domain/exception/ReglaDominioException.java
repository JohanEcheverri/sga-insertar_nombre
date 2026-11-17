package co.uniquindio.edu.sga.domain.exception;

/** Indica que una operación incumple una regla del dominio. */
public class ReglaDominioException extends DomainException {

    public ReglaDominioException(String message) {
        super(message);
    }

    public ReglaDominioException(String message, Throwable cause) {
        super(message, cause);
    }
}