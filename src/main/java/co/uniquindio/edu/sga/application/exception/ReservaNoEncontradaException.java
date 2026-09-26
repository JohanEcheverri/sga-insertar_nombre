package co.edu.uniquindio.sga.application.exception;

import co.uniquindio.edu.sga.domain.valueobject.CodigoReserva;
public class ReservaNoEncontradaException extends RuntimeException {

    public ReservaNoEncontradaException(CodigoReserva codigo) {
        super("No e encontró la reserva con el código: " + codigo);
    }
}