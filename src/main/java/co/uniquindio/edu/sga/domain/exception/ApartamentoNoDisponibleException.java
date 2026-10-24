package co.uniquindio.edu.sga.domain.exception;

public class ApartamentoNoDisponibleException extends DomainException {

    public ApartamentoNoDisponibleException(String message) {
        super(message);
    }

    public ApartamentoNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}