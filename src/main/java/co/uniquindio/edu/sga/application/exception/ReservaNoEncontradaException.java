package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
public class ReservaNoEncontradaException extends RuntimeException {

    public ReservaNoEncontradaException(CodigoReserva codigo) {
        super("No e encontró la reserva con el código: " + codigo);
    }
}