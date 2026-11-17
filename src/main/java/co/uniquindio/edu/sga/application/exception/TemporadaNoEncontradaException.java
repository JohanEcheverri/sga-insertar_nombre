package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo temporada solicitado por la aplicación. */
public class TemporadaNoEncontradaException extends ApplicationException {

    public TemporadaNoEncontradaException(String id) {
        super(String.format("No se encontró la temporada con el identificador: %s", id));
    }

    public TemporadaNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la temporada con el identificador: %s", id), cause);
    }
}
