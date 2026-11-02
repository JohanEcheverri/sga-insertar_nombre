package co.uniquindio.edu.sga.application.exception;

public class BloqueoNoEncontradoException extends ApplicationException {

    public BloqueoNoEncontradoException(String id) {
        super(String.format("No se encontró el bloqueo con el identificador: %s", id));
    }

    public BloqueoNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el bloqueo con el identificador: %s", id), cause);
    }
}
