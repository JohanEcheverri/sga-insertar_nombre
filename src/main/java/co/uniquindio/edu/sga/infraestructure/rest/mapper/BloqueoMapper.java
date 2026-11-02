package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.application.dto.request.CrearBloqueoRequest;
import co.uniquindio.edu.sga.application.dto.response.BloqueoResponse;
import co.uniquindio.edu.sga.application.result.BloqueoDetalle;
import co.uniquindio.edu.sga.application.usecase.CrearBloqueoUseCase;
import co.uniquindio.edu.sga.application.security.Solicitante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

/** Convierte solicitudes y resultados de bloqueos a los modelos públicos de la API. */
@Mapper(componentModel = "spring")
public interface BloqueoMapper {

    @Mapping(target = "fechaInicio", source = "desde")
    @Mapping(target = "fechaFin", source = "hasta")
    BloqueoResponse aRespuesta(BloqueoDetalle bloqueo);

    default CrearBloqueoUseCase.Command aComando(String apartamentoId, CrearBloqueoRequest solicitud, Solicitante solicitante) {
        return new CrearBloqueoUseCase.Command(
                UUID.fromString(apartamentoId), solicitud.fechaInicio(), solicitud.fechaFin(), solicitud.motivo(), solicitante);
    }
}
