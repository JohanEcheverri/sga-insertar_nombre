package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.TramoCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.VersionPolitica;

import java.util.List;

/** Convierte la política de cancelación entre el dominio y los modelos JSON de REST. */
public interface PoliticaCancelacionMapper {
    static PoliticaCancelacion aDominio(PoliticaCancelacionRequest solicitud) {
        return new PoliticaCancelacion(
                new VersionPolitica(solicitud.version()),
                solicitud.tramos().stream()
                        .map(tramo -> new TramoCancelacion(tramo.diasAntelacionMinimos(), tramo.porcentajeRetencion()))
                        .toList(),
                solicitud.porcentajeRetencionNoShow());
    }

    static PoliticaCancelacionResponse aRespuesta(PoliticaCancelacion politica) {
        return new PoliticaCancelacionResponse(politica.version().valor(), politica.tramos(),
                politica.porcentajeRetencionNoShow());
    }

    record PoliticaCancelacionRequest(String version, List<TramoRequest> tramos,
                                      double porcentajeRetencionNoShow) { }

    record TramoRequest(int diasAntelacionMinimos, double porcentajeRetencion) { }

    record PoliticaCancelacionResponse(String version, List<TramoCancelacion> tramos,
                                       double porcentajeRetencionNoShow) { }
}
