package co.uniquindio.edu.sga.application.exception;

public class FolioNoEncontradoException extends ApplicationException {

    public FolioNoEncontradoException(String id) {
        super(String.format("No se encontró el folio de la reserva con el identificador: %s", id));
    }

    public FolioNoEncontradoException(String id, Throwable cause) {
        super(String.format("No se encontró el folio de la reserva con el identificador: %s", id), cause);
    }
}
