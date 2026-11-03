package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.BloqueoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acceso Spring Data a los bloqueos de calendario. */
public interface BloqueoJpaDataRepository extends JpaRepository<BloqueoEntity, String> {
    /** Busca bloqueos de un apartamento, opcionalmente limitados a los activos. */
    List<BloqueoEntity> findByApartamentoId(String apartamentoId);
    List<BloqueoEntity> findByApartamentoIdAndActivoTrue(String apartamentoId);
}
