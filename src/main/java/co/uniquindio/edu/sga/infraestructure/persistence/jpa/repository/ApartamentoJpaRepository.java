package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.repository.ApartamentoRepository;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper.ApartamentoPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.*;

/** Adaptador de persistencia relacional para el puerto de apartamentos. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class ApartamentoJpaRepository implements ApartamentoRepository {
    private final ApartamentoJpaDataRepository dataRepository;
    private final ApartamentoPersistenceMapper mapper;
    /** Guarda el estado actual del apartamento. */
    public Apartamento guardar(Apartamento a) { dataRepository.save(mapper.aEntidad(a)); return a; }
    /** Busca un apartamento por su identificador de dominio. */
    @Transactional(readOnly=true) public Optional<Apartamento> obtenerPorIdentificacion(ApartamentoId id) { return dataRepository.findById(id.valor().toString()).map(mapper::aDominio); }
    /** Lista todos los apartamentos persistidos. */
    @Transactional(readOnly=true) public List<Apartamento> listarTodos() { return dataRepository.findAll().stream().map(mapper::aDominio).toList(); }
    /** Retira lógicamente el apartamento para conservar su historial. */
    public void eliminar(ApartamentoId id) { obtenerPorIdentificacion(id).ifPresent(a -> { a.retirar(); guardar(a); }); }
}
