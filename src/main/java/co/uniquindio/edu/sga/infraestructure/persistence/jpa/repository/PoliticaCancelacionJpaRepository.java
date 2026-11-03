package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.repository.PoliticaCancelacionRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.PoliticaCancelacionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

/** Adaptador JPA para consultar versiones inmutables de políticas de cancelación. */
@Repository @Primary @Transactional(readOnly=true) @RequiredArgsConstructor
public class PoliticaCancelacionJpaRepository implements PoliticaCancelacionRepository {
    private final PoliticaCancelacionJpaDataRepository data;
    /** Devuelve la versión vigente en la fecha indicada. */
    public Optional<PoliticaCancelacion> obtenerVigenteEn(LocalDate fecha) { return data.findFirstByVigenteDesdeLessThanEqualOrderByVigenteDesdeDesc(fecha).map(this::map); }
    /** Busca una versión concreta de la política. */
    public Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version) { return data.findById(version.valor()).map(this::map); }
    private PoliticaCancelacion map(PoliticaCancelacionEntity e) { return new PoliticaCancelacion(new VersionPolitica(e.getVersion(),e.getVigenteDesde()),e.getTramos().stream().map(t->new TramoCancelacion(t.diasAntelacionMinimos(),t.porcentajeRetencion())).toList(),e.getPorcentajeNoShow()); }
}
