package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.Cargas;
import co.uniquindio.edu.sga.domain.repository.AlojamientoRepository;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import org.springframework.stereotype.Service;

@Service
public class ConsultarPoliticaCancelacionUseCase {
    private final AlojamientoRepository alojamientos;
    public ConsultarPoliticaCancelacionUseCase(AlojamientoRepository alojamientos) { this.alojamientos = alojamientos; }
    public PoliticaCancelacion ejecutar(Solicitante solicitante) {
        if (solicitante == null || !solicitante.esAdministrador()) throw new AccesoDenegadoException("Solo administración puede consultar la política.");
        return Cargas.alojamiento(alojamientos).getPoliticaVigente();
    }
}
