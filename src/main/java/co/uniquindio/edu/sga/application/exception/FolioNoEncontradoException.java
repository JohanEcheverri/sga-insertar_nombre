package co.uniquindio.edu.sga.application.exception;

/** Indica que no se encontró el recurso de tipo folio solicitado por la aplicación. */
public class FolioNoEncontradoException extends ApplicationException {

    public FolioNoEncontradoException(String id) {
        super(String.format("No se encontró el folio de la reserva con el identificador: %s", id));
    }

    public FolioNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el folio de la reserva con el identificador: %s", id), cause);
    }
}
