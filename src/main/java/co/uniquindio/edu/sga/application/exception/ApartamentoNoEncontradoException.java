package co.uniquindio.edu.sga.application.exception;

public class ApartamentoNoEncontradoException extends ApplicationException {

    public ApartamentoNoEncontradoException(String id) {
        super(String.format("No se encontró el apartamento con el identificador: %s", id));
    }

    public ApartamentoNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el apartamento con el identificador: %s", id), cause);
    }
}
