package co.uniquindio.edu.sga.application.exception;

public class NovedadNoEncontradaException extends ApplicationException {

    public NovedadNoEncontradaException(String id) {
        super(String.format("No se encontró la novedad con el identificador: %s", id));
    }

    public NovedadNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la novedad con el identificador: %s", id), cause);
    }
}
