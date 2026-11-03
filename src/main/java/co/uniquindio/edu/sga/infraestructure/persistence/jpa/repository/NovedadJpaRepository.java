package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.repository.NovedadRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper.NovedadPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.*;

/** Adaptador JPA de los reportes de novedades de apartamentos. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class NovedadJpaRepository implements NovedadRepository {
    private final NovedadJpaDataRepository dataRepository;
    private final NovedadPersistenceMapper mapper;
    /** Guarda o actualiza una novedad. */
    public Novedad guardar(Novedad n) { dataRepository.save(mapper.aEntidad(n)); return n; }
    /** Busca una novedad por identificador. */
    @Transactional(readOnly=true) public Optional<Novedad> buscarPorId(NovedadId id) { return dataRepository.findById(id.valor().toString()).map(mapper::aDominio); }
    /** Lista las novedades no anuladas de un apartamento. */
    @Transactional(readOnly=true) public List<Novedad> buscarPorApartamento(ApartamentoId id) { return dataRepository.findByApartamentoIdAndAnuladaFalse(id.valor().toString()).stream().map(mapper::aDominio).toList(); }
}
