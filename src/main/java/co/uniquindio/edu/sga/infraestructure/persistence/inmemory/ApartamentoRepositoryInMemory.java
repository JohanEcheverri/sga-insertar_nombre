package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.repository.ApartamentoRepository;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** Adaptador en memoria del puerto (Guía 06), bean de Spring (Guía 09) */
@Repository
public class ApartamentoRepositoryInMemory implements ApartamentoRepository {

    private final HashMap<ApartamentoId, Apartamento> apartamentos = new HashMap<>();

    @Override
    public Apartamento guardar(Apartamento apartamento) {
        // En memoria, guardamos el apartamento asociado a su ID y retornamos la instancia
        apartamentos.put(apartamento.getId(), apartamento);
        return apartamento;
    }

    @Override
    public Optional<Apartamento> obtenerPorIdentificacion(ApartamentoId identificacion) {
        return Optional.ofNullable(apartamentos.get(identificacion));
    }

    @Override
    public List<Apartamento> listarTodos() {
        return new ArrayList<>(apartamentos.values());
    }

    @Override
    public void eliminar(ApartamentoId identificacion) {
        apartamentos.remove(identificacion);
    }
}