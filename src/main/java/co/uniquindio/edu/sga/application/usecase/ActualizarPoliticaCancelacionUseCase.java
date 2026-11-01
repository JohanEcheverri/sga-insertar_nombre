package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;

/** Actualiza la política vigente con una versión nueva sin alterar reservas existentes. */
public class ActualizarPoliticaCancelacionUseCase {
    public PoliticaCancelacion ejecutar(Alojamiento alojamiento, PoliticaCancelacion nuevaPolitica) {
        if (alojamiento == null) {
            throw new ReglaDominioException("El alojamiento es obligatorio.");
        }
        if (nuevaPolitica == null) {
            throw new ReglaDominioException("La nueva política de cancelación es obligatoria.");
        }
        if (alojamiento.getPoliticaVigente().version().equals(nuevaPolitica.version())) {
            throw new ReglaDominioException("La política actualizada debe tener una versión nueva.");
        }

        alojamiento.cambiarPoliticaVigente(nuevaPolitica);
        return alojamiento.getPoliticaVigente();
    }
}
