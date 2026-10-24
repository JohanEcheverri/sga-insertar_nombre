package co.uniquindio.edu.sga.application.exception;

public class AlojamientoNoEncontradoException extends ApplicationException {

    public AlojamientoNoEncontradoException(String id) {
        super(String.format("No se encontró el alojamiento con el identificador: %s", id));
    }

    public AlojamientoNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el alojamiento con el identificador: %s", id), cause);
    }
}