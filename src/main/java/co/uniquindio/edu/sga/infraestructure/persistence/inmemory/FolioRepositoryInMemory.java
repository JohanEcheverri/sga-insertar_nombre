package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.time.LocalDateTime;
import java.util.*;

public class FolioRepositoryInMemory implements FolioRepository {

    private final Map<FolioId, Folio> folios = new LinkedHashMap<>();

    @Override
    public void guardar(Folio folio) {
        folios.put(folio.getId(), folio);
    }

    @Override
    public Optional<Folio> buscarPorId(FolioId id) {
        return Optional.ofNullable(folios.get(id));
    }

    @Override
    public Optional<Folio> buscarPorReservaId(ReservaId reservaId) {
        return folios.values().stream().filter(f -> f.getReservaId().equals(reservaId)).findFirst();
    }

    @Override
    public List<Folio> buscarConPagosEntre(LocalDateTime desde, LocalDateTime hasta) {
        return folios.values().stream().filter(f -> f.getPagos().stream()
                .anyMatch(p -> !p.fechaRegistro().isBefore(desde) && p.fechaRegistro().isBefore(hasta))).toList();
    }

    @Override
    public List<Folio> buscarTodos() {
        return List.copyOf(folios.values());
    }
}
