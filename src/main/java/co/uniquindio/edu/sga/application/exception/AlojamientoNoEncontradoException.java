package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo alojamiento solicitado por la aplicación. */
public class AlojamientoNoEncontradoException extends ApplicationException {

    public AlojamientoNoEncontradoException(String id) {
        super(String.format("No se encontró el alojamiento con el identificador: %s", id));
    }

    public AlojamientoNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el alojamiento con el identificador: %s", id), cause);
    }
}