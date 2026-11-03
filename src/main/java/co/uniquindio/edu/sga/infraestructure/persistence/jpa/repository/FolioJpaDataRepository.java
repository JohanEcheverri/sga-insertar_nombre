package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.FolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acceso Spring Data a los folios. */
public interface FolioJpaDataRepository extends JpaRepository<FolioEntity, String> {
    /** Busca el folio asociado a una reserva. */
    Optional<FolioEntity> findByReservaId(String reservaId);
}
