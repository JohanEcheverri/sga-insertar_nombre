package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo novedad solicitado por la aplicación. */
public class NovedadNoEncontradaException extends ApplicationException {

    public NovedadNoEncontradaException(String id) {
        super(String.format("No se encontró la novedad con el identificador: %s", id));
    }

    public NovedadNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la novedad con el identificador: %s", id), cause);
    }
}
