package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.valueobject.TemporadaId;

import java.util.List;
import java.util.Optional;

public interface TemporadaRepository {

    Temporada guardar(Temporada temporada);

    Optional<Temporada> obtenerPorId(TemporadaId id);

    List<Temporada> listarTodas();

    void eliminar(TemporadaId id);
}