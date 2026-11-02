package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.TramoCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.VersionPolitica;
import co.uniquindio.edu.sga.application.dto.request.DefinirPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.request.ActualizarPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.response.PoliticaCancelacionResponse;
import co.uniquindio.edu.sga.application.dto.response.TramoCancelacionResponse;

import java.util.List;

/** Convierte la política de cancelación entre el dominio y los modelos JSON de REST. */
public interface PoliticaCancelacionMapper {
    static PoliticaCancelacion aDominio(DefinirPoliticaCancelacionRequest solicitud) {
        return crearPolitica(solicitud.version(), solicitud.tramos(), solicitud.porcentajeRetencionNoShow());
    }

    static PoliticaCancelacion aDominio(ActualizarPoliticaCancelacionRequest solicitud) {
        return crearPolitica(solicitud.version(), solicitud.tramos(), solicitud.porcentajeRetencionNoShow());
    }

    private static PoliticaCancelacion crearPolitica(String version,
            List<co.uniquindio.edu.sga.application.dto.request.TramoCancelacionRequest> tramos,
            Double porcentajeRetencionNoShow) {
        return new PoliticaCancelacion(
                new VersionPolitica(version),
                tramos.stream()
                        .map(tramo -> new TramoCancelacion(tramo.diasAntelacionMinimos(), tramo.porcentajeRetencion()))
                        .toList(),
                porcentajeRetencionNoShow);
    }

    static PoliticaCancelacionResponse aRespuesta(PoliticaCancelacion politica) {
        List<TramoCancelacionResponse> tramos = politica.tramos().stream()
                .map(t -> new TramoCancelacionResponse(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList();
        return new PoliticaCancelacionResponse(politica.version().valor(), tramos,
                politica.porcentajeRetencionNoShow());
    }
}
