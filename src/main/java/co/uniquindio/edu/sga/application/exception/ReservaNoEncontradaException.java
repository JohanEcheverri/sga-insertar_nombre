package co.uniquindio.edu.sga.application.exception;

public class ReservaNoEncontradaException extends ApplicationException {

    public ReservaNoEncontradaException(String id) {
        super(String.format("No se encontró la reserva con el identificador: %s", id));
    }

    public ReservaNoEncontradaException(String id, Throwable cause) {
        super(String.format("No se encontró la reserva con el identificador: %s", id), cause);
    }
}