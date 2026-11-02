package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class FolioRepositoryInMemory implements FolioRepository {

    private final HashMap<ReservaId, Folio> folios = new HashMap<>();

    @Override
    public Optional<Folio> obtenerPorReservaCodigo(ReservaId reserva) {
        return Optional.ofNullable(folios.get(reserva));
    }

    @Override
    public Folio guardar(Folio folio) {
        folios.put(folio.getReservaId(), folio);
        return folio; // Retornamos el folio guardado para cumplir con la firma del método
    }

    @Override
    public List<Folio> listarTodos() {
        return new ArrayList<>(folios.values());
    }

    @Override
    public Optional<Folio> obtenerPorId(ReservaId id) {
        return Optional.ofNullable(folios.get(id));
    }
}