package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.TemporadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso Spring Data a temporadas y sus tarifas. */
public interface TemporadaJpaDataRepository extends JpaRepository<TemporadaEntity, String> { }
