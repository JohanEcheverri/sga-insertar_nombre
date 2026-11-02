package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.NovedadId;

import java.util.List;
import java.util.Optional;

public interface NovedadRepository {

    void guardar(Novedad novedad);

    Optional<Novedad> buscarPorId(NovedadId id);

    /** Incluye anuladas; el caso de uso decide qué mostrar. Más recientes primero. */
    List<Novedad> buscarPorApartamento(ApartamentoId apartamentoId);
}
