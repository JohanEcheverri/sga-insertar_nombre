package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.valueobject.BloqueoId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class BloqueoRepositoryInMemory implements BloqueoRepository {

    private final HashMap<BloqueoId, Bloqueo> bloqueos = new HashMap<>();

    @Override
    public List<Bloqueo> buscarVigentesPorApartamento(ApartamentoId apartamento, RangoFechas periodo) {
        return bloqueos.values()
                .stream()
                .filter(bloqueo -> bloqueo.getApartamentoId().equals(apartamento))
                .filter(Bloqueo::isActivo)
                .filter(bloqueo -> bloqueo.getRangoFechas().intersecta(periodo))
                .toList();
    }

    public void guardar(Bloqueo bloqueo) {
        bloqueos.put(bloqueo.getId(), bloqueo);
    }

    @Override
    public Optional<Bloqueo> obtenerPorId(BloqueoId id) {
        return Optional.ofNullable(bloqueos.get(id));
    }

    @Override
    public List<Bloqueo> listarBloqueos() {
        return new ArrayList<>(bloqueos.values());
    }

    @Override
    public void eliminar(BloqueoId id) {
        bloqueos.remove(id);
    }

}