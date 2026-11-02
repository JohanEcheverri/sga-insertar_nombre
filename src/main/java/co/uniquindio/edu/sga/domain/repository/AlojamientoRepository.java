package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;

import java.util.Optional;

/**
 * F-13: el sistema administra UN único alojamiento. Apartamentos, temporadas y tarifas viven dentro
 * del agregado, por lo que se cargan y se guardan a través de este puerto.
 */
public interface AlojamientoRepository {

    Optional<Alojamiento> obtener();

    void guardar(Alojamiento alojamiento);
}
