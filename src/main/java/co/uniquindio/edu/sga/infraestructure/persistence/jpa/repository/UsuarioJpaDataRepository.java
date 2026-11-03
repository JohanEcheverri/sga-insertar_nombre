package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, String> {
    Optional<UsuarioEntity> findByCorreo(String correo);
}
