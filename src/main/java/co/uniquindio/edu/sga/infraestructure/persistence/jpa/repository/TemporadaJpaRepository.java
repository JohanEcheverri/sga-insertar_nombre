package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.repository.TemporadaRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/** Adaptador JPA para temporadas y sus tarifas. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class TemporadaJpaRepository implements TemporadaRepository {
    private final TemporadaJpaDataRepository data;
    /** Guarda una temporada con sus tarifas asociadas. */
    public Temporada guardar(Temporada t) { data.save(aEntidad(t)); return t; }
    /** Busca una temporada por identificador. */
    @Transactional(readOnly=true) public Optional<Temporada> obtenerPorId(TemporadaId id) { return data.findById(id.valor().toString()).map(this::aDominio); }
    /** Devuelve todas las temporadas registradas. */
    @Transactional(readOnly=true) public List<Temporada> listarTodas() { return data.findAll().stream().map(this::aDominio).toList(); }
    /** Elimina una temporada por identificador. */
    public void eliminar(TemporadaId id) { data.deleteById(id.valor().toString()); }
    private TemporadaEntity aEntidad(Temporada t) { var e=new TemporadaEntity(); e.setId(t.getId().valor().toString()); e.setNombre(t.getNombre()); e.setEsBase(t.esBase()); if(t.getRangoFechas()!=null){e.setFechaInicio(t.getRangoFechas().fechaInicio());e.setFechaFin(t.getRangoFechas().fechaFin());} t.getTarifas().forEach(tarifa->{var x=new TarifaEmbeddable();x.setApartamentoId(tarifa.apartamentoId().valor().toString());x.setPrecioPorNoche(tarifa.precioPorNochePorOcupante().valor());e.getTarifas().add(x);}); return e; }
    private Temporada aDominio(TemporadaEntity e) { var id=TemporadaId.of(UUID.fromString(e.getId())); var tarifas=e.getTarifas().stream().map(x->new Tarifa(id,ApartamentoId.of(UUID.fromString(x.getApartamentoId())),Dinero.de(x.getPrecioPorNoche()))).toList(); return e.isEsBase()?Temporada.crearBase(id,tarifas):Temporada.crear(id,e.getNombre(),new RangoFechas(e.getFechaInicio(),e.getFechaFin()),tarifas); }
}
