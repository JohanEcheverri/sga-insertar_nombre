package co.uniquindio.edu.sga.domain.exception;

public class AforoExcedidoException extends DomainException {

    public AforoExcedidoException(int aforoMax, int ocupantesSolicitados) {
        super(String.format("El número de ocupantes (%d) excede el aforo máximo permitido (%d) para el apartamento.",
                ocupantesSolicitados, aforoMax));
    }

    public AforoExcedidoException(String message, Throwable cause) {
        super(message, cause);
    }
}