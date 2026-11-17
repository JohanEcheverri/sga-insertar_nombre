package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.ApartamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Repositorio Spring Data para consultar entidades de apartamento. */
public interface ApartamentoJpaDataRepository extends JpaRepository<ApartamentoEntity, String> {
    List<ApartamentoEntity> findByActivoTrue();
}
