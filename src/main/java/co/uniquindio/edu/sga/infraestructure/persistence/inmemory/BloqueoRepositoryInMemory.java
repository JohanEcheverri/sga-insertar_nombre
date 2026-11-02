package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BloqueoRepositoryInMemory implements BloqueoRepository {

    private final Map<BloqueoId, Bloqueo> bloqueos = new LinkedHashMap<>();

    @Override
    public void guardar(Bloqueo bloqueo) {
        bloqueos.put(bloqueo.getId(), bloqueo);
    }

    @Override
    public Optional<Bloqueo> buscarPorId(BloqueoId id) {
        return Optional.ofNullable(bloqueos.get(id));
    }

    @Override
    public List<Bloqueo> buscarPorApartamento(ApartamentoId apartamentoId) {
        return bloqueos.values().stream().filter(b -> b.getApartamentoId().equals(apartamentoId)).toList();
    }

    @Override
    public List<Bloqueo> buscarActivosPorApartamento(ApartamentoId apartamentoId) {
        return buscarPorApartamento(apartamentoId).stream().filter(Bloqueo::isActivo).toList();
    }
}
