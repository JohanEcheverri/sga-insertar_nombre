package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.AlojamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso Spring Data al agregado raíz del alojamiento. */
public interface AlojamientoJpaDataRepository extends JpaRepository<AlojamientoEntity, String> { }
