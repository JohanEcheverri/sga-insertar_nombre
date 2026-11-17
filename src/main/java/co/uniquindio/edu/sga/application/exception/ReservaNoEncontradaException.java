package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo reserva solicitado por la aplicación. */
public class ReservaNoEncontradaException extends ApplicationException {

    public ReservaNoEncontradaException(String id) {
        super(String.format("No se encontró la reserva con el identificador: %s", id));
    }

    public ReservaNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la reserva con el identificador: %s", id), cause);
    }
}