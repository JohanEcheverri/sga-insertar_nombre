package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.valueobject.AlojamientoId;

import java.util.List;
import java.util.Optional;

public interface AlojamientoRepository {

    Alojamiento guardar(Alojamiento alojamiento);

    Optional<Alojamiento> obtenerPorId(AlojamientoId id);

    List<Alojamiento> listarTodos();

    void eliminar(AlojamientoId id);
}