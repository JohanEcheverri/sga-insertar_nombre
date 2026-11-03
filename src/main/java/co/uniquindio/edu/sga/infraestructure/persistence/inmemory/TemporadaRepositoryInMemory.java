package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.repository.TemporadaRepository;
import co.uniquindio.edu.sga.domain.valueobject.TemporadaId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class TemporadaRepositoryInMemory implements TemporadaRepository {

    private final HashMap<TemporadaId, Temporada> temporadas = new HashMap<>();

    @Override
    public Temporada guardar(Temporada temporada) {
        temporadas.put(temporada.getId(), temporada);
        return temporada;
    }

    @Override
    public Optional<Temporada> obtenerPorId(TemporadaId id) {
        return Optional.ofNullable(temporadas.get(id));
    }

    @Override
    public List<Temporada> listarTodas() {
        return new ArrayList<>(temporadas.values());
    }

    @Override
    public void eliminar(TemporadaId id) {
        temporadas.remove(id);
    }
}