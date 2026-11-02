package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;

import java.util.List;
import java.util.Optional;

/** Puerto de persistencia de Bloqueo (era una clase; debe ser interfaz). */
public interface BloqueoRepository {

    void guardar(Bloqueo bloqueo);

    Optional<Bloqueo> buscarPorId(BloqueoId id);

    List<Bloqueo> buscarPorApartamento(ApartamentoId apartamentoId);

    /** Solo bloqueos vigentes (activos). */
    List<Bloqueo> buscarActivosPorApartamento(ApartamentoId apartamentoId);
}
