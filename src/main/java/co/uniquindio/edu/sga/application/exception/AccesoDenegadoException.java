package co.uniquindio.edu.sga.application.exception;

/** El mensaje describe el motivo en lenguaje de negocio (la capa REST lo traduce a 403 / 409). */
public class AccesoDenegadoException extends ApplicationException {

    public AccesoDenegadoException(String message) {
        super(message);
    }

    public AccesoDenegadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
