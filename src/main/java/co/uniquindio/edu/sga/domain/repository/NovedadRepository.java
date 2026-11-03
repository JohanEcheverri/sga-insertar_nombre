package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.valueobject.AlojamientoId;
import co.uniquindio.edu.sga.domain.valueobject.NovedadId;

import java.util.List;
import java.util.Optional;

public interface NovedadRepository {

    Novedad guardar(Novedad novedad);

    Optional<Novedad> obtenerPorId(NovedadId id);

    List<Novedad> listarPorAlojamiento(AlojamientoId alojamientoId);
}