package co.uniquindio.edu.sga.application.exception;

public class TemporadaNoEncontradaException extends ApplicationException {

    public TemporadaNoEncontradaException(String id) {
        super(String.format("No se encontró la temporada con el identificador: %s", id));
    }

    public TemporadaNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la temporada con el identificador: %s", id), cause);
    }
}
