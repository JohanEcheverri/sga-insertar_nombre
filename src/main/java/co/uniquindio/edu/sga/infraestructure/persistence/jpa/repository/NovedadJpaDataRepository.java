package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.NovedadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NovedadJpaDataRepository extends JpaRepository<NovedadEntity, String> {
    List<NovedadEntity> findByApartamentoIdAndAnuladaFalse(String apartamentoId);
}
