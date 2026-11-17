package co.uniquindio.edu.sga.domain.exception;

/** Excepción base para fallos de las reglas del dominio. */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}