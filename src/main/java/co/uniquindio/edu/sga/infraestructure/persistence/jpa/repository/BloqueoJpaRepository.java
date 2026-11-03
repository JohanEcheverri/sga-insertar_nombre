package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.BloqueoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/** Adaptador JPA para persistir los bloqueos de disponibilidad. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class BloqueoJpaRepository implements BloqueoRepository {
    private final BloqueoJpaDataRepository data;
    /** Persiste un bloqueo conservando su identificador de dominio. */
    public void guardar(Bloqueo b) { var e=new BloqueoEntity(); e.setId(b.getId().valor().toString()); e.setApartamentoId(b.getApartamentoId().valor().toString()); e.setFechaInicio(b.getRangoFechas().fechaInicio()); e.setFechaFin(b.getRangoFechas().fechaFin()); e.setMotivo(b.getMotivo()); e.setActivo(b.isActivo()); data.save(e); }
    /** Busca un bloqueo por identificador. */
    @Transactional(readOnly=true) public Optional<Bloqueo> buscarPorId(BloqueoId id) { return data.findById(id.valor().toString()).map(this::map); }
    /** Lista los bloqueos asociados a un apartamento. */
    @Transactional(readOnly=true) public List<Bloqueo> buscarPorApartamento(ApartamentoId id) { return data.findByApartamentoId(id.valor().toString()).stream().map(this::map).toList(); }
    /** Lista los bloqueos activos asociados a un apartamento. */
    @Transactional(readOnly=true) public List<Bloqueo> buscarActivosPorApartamento(ApartamentoId id) { return data.findByApartamentoIdAndActivoTrue(id.valor().toString()).stream().map(this::map).toList(); }
    private Bloqueo map(BloqueoEntity e) { var b=Bloqueo.crear(BloqueoId.of(UUID.fromString(e.getId())), ApartamentoId.of(UUID.fromString(e.getApartamentoId())), new RangoFechas(e.getFechaInicio(),e.getFechaFin()),e.getMotivo()); if(!e.isActivo()) b.desactivar(); return b; }
}
