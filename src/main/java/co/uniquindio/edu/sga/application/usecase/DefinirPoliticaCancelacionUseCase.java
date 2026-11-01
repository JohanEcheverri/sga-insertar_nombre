package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;

/** Establece la política vigente del alojamiento. */
public class DefinirPoliticaCancelacionUseCase {
    public PoliticaCancelacion ejecutar(Alojamiento alojamiento, PoliticaCancelacion politica) {
        if (alojamiento == null) {
            throw new ReglaDominioException("El alojamiento es obligatorio.");
        }
        alojamiento.cambiarPoliticaVigente(politica);
        return alojamiento.getPoliticaVigente();
    }
}
