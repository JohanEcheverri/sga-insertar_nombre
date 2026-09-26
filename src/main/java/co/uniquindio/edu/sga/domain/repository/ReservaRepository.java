package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.CodigoReserva;
import co.uniquindio.edu.sga.domain.valueobject.IdentificacionApartamento;
import co.uniquindio.edu.sga.domain.valueobject.Periodo;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento, Periodo periodo);
    Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo);

    void guardar(Reserva reserva);
}
