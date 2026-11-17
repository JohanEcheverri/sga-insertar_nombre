package co.uniquindio.edu.sga.domain.exception;

/** Indica que el apartamento no está disponible para el periodo solicitado. */
public class ApartamentoNoDisponibleException extends DomainException {

    public ApartamentoNoDisponibleException(String message) {
        super(message);
    }

    public ApartamentoNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}