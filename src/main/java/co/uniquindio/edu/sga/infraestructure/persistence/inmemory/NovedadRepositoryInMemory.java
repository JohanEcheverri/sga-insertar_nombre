package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.repository.NovedadRepository;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.NovedadId;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class NovedadRepositoryInMemory implements NovedadRepository {

    private final Map<NovedadId, Novedad> novedades = new LinkedHashMap<>();

    @Override
    public void guardar(Novedad novedad) {
        novedades.put(novedad.getId(), novedad);
    }

    @Override
    public Optional<Novedad> buscarPorId(NovedadId id) {
        return Optional.ofNullable(novedades.get(id));
    }

    @Override
    public List<Novedad> buscarPorApartamento(ApartamentoId apartamentoId) {
        return novedades.values().stream().filter(n -> n.getApartamentoId().equals(apartamentoId)).toList();
    }
}
