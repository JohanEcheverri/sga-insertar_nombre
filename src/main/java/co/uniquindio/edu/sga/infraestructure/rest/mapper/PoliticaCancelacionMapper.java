package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.TramoCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.VersionPolitica;
import co.uniquindio.edu.sga.application.dto.request.DefinirPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.request.ActualizarPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.usecase.ActualizarPoliticaCancelacionUseCase;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.dto.response.PoliticaCancelacionResponse;
import co.uniquindio.edu.sga.application.dto.response.TramoCancelacionResponse;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

/** Convierte la política de cancelación entre el dominio y los modelos JSON de REST. */
@Mapper(componentModel = "spring")
public interface PoliticaCancelacionMapper {
    default ActualizarPoliticaCancelacionUseCase.Command aComando(DefinirPoliticaCancelacionRequest solicitud, Solicitante solicitante) {
        return new ActualizarPoliticaCancelacionUseCase.Command(solicitud.tramos().stream()
                .map(t -> new ActualizarPoliticaCancelacionUseCase.Tramo(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList(), solicitud.porcentajeRetencionNoShow(), solicitante);
    }

    default ActualizarPoliticaCancelacionUseCase.Command aComando(ActualizarPoliticaCancelacionRequest solicitud, Solicitante solicitante) {
        return new ActualizarPoliticaCancelacionUseCase.Command(solicitud.tramos().stream()
                .map(t -> new ActualizarPoliticaCancelacionUseCase.Tramo(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList(), solicitud.porcentajeRetencionNoShow(), solicitante);
    }

    default PoliticaCancelacion aDominio(DefinirPoliticaCancelacionRequest solicitud) {
        return crearPolitica(solicitud.version(), LocalDate.now(), solicitud.tramos(), solicitud.porcentajeRetencionNoShow());
    }

    default PoliticaCancelacion aDominio(ActualizarPoliticaCancelacionRequest solicitud) {
        return crearPolitica(solicitud.version(), LocalDate.now(), solicitud.tramos(), solicitud.porcentajeRetencionNoShow());
    }

    private static PoliticaCancelacion crearPolitica(String version, LocalDate fechaVigencia,
                                                     List<co.uniquindio.edu.sga.application.dto.request.TramoCancelacionRequest> tramos,
                                                     BigDecimal porcentajeRetencionNoShow) {
        return new PoliticaCancelacion(
                new VersionPolitica(version, fechaVigencia), // <-- Añades la fecha de vigencia aquí
                tramos.stream()
                        .map(tramo -> new TramoCancelacion(tramo.diasAntelacionMinimos(), tramo.porcentajeRetencion()))
                        .toList(),
                porcentajeRetencionNoShow);
    }

    default PoliticaCancelacionResponse aRespuesta(PoliticaCancelacion politica) {
        List<TramoCancelacionResponse> tramos = politica.tramos().stream()
                .map(t -> new TramoCancelacionResponse(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList();
        return new PoliticaCancelacionResponse(politica.version().valor(), tramos,
                politica.porcentajeRetencionNoShow());
    }

    default PoliticaCancelacionResponse aRespuesta(List<co.uniquindio.edu.sga.application.dto.request.TramoCancelacionRequest> tramos,
                                                   BigDecimal retencionNoShow, String version) {
        return new PoliticaCancelacionResponse(version, tramos.stream()
                .map(t -> new TramoCancelacionResponse(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList(), retencionNoShow);
    }
}
