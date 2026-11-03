package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.PoliticaCancelacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

/** Acceso Spring Data a las versiones de política de cancelación. */
public interface PoliticaCancelacionJpaDataRepository extends JpaRepository<PoliticaCancelacionEntity, String> {
    /** Obtiene la última política cuya vigencia comenzó antes o en la fecha indicada. */
    Optional<PoliticaCancelacionEntity> findFirstByVigenteDesdeLessThanEqualOrderByVigenteDesdeDesc(LocalDate fecha);
}
