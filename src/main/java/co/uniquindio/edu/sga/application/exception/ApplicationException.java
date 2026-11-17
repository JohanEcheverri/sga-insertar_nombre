package co.uniquindio.edu.sga.application.exception;

/** Excepción base para errores propios de la capa de aplicación. */
public abstract class ApplicationException extends RuntimeException {

    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}