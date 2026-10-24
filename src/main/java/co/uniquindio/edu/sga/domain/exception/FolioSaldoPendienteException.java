package co.uniquindio.edu.sga.domain.exception;

public class FolioSaldoPendienteException extends DomainException {

    public FolioSaldoPendienteException(String folioId) {
        super(String.format("No se puede cerrar el folio [%s] porque registra un saldo pendiente de pago.", folioId));
    }

    public FolioSaldoPendienteException(String message, Throwable cause) {
        super(message, cause);
    }
}