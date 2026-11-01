package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;

import java.util.Optional;
import java.util.List;

public interface BloqueoRepository {

    void guardar(Bloqueo bloqueo);

    Optional<Bloqueo> obtenerPorId(BloqueoId id);

    List<Bloqueo> listarBloqueos();

    List<Bloqueo> buscarVigentesPorApartamento(ApartamentoId apartamento, RangoFechas periodo);

    void eliminar(BloqueoId id);
}