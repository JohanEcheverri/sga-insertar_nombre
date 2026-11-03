package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import java.util.UUID;

@Repository
public interface ReservaJpaDataRepository extends JpaRepository<ReservaEntity, Long> {

    Optional<ReservaEntity> findByCodigo(UUID codigo);

    List<ReservaEntity> findByEstado(EstadoReserva estado);

    List<ReservaEntity> findByApartamentoIdAndEstadoIn(UUID apartamentoId, List<EstadoReserva> estados);
}
