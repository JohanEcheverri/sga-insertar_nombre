package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.util.Optional;

/** Puerto de persistencia para el agregado Reserva. */
public interface ReservaRepository {
    Optional<Reserva> obtenerPorCodigo(ReservaId codigo);
}
