package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.repository.AlojamientoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Implementación local (I-02): sin base de datos ni internet. Un único alojamiento (F-13). */
@Repository
public class AlojamientoRepositoryInMemory implements AlojamientoRepository {

    private Alojamiento alojamiento;

    @Override
    public Optional<Alojamiento> obtener() {
        return Optional.ofNullable(alojamiento);
    }

    @Override
    public void guardar(Alojamiento alojamiento) {
        this.alojamiento = alojamiento;
    }
}
