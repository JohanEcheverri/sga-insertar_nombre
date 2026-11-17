package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo apartamento solicitado por la aplicación. */
public class ApartamentoNoEncontradoException extends ApplicationException {

    public ApartamentoNoEncontradoException(String id) {
        super(String.format("No se encontró el apartamento con el identificador: %s", id));
    }

    public ApartamentoNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el apartamento con el identificador: %s", id), cause);
    }
}
