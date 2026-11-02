package co.uniquindio.edu.sga.application.exception;

/** El mensaje describe el motivo en lenguaje de negocio (la capa REST lo traduce a 403 / 409). */
public class RecursoDuplicadoException extends ApplicationException {

    public RecursoDuplicadoException(String message) {
        super(message);
    }

    public RecursoDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
