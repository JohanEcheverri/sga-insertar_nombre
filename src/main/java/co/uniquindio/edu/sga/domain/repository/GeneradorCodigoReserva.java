package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

/** Puerto del dominio: genera el código de la reserva (Guía 06) */
public interface GeneradorCodigoReserva {

    ReservaId generar();
}
