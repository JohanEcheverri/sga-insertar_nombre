package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    Reserva guardar(Reserva reserva);

    Optional<Reserva> obtenerPorCodigo(ReservaId codigo);

    List<Reserva> listarTodas();

    List<Reserva> buscarActivasPorApartamento(ApartamentoId apartamento, RangoFechas periodo);

    void eliminar(ReservaId codigo);
}