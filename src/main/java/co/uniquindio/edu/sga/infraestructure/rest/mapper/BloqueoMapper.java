package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.application.dto.response.BloqueoResponse;

/** Convierte Bloqueo al modelo público de la API. */
public interface BloqueoMapper {
    static BloqueoResponse aRespuesta(Bloqueo bloqueo) {
        return new BloqueoResponse(bloqueo.getId().valor().toString(),
                bloqueo.getApartamentoId().valor().toString(), bloqueo.getRangoFechas().fechaInicio(),
                bloqueo.getRangoFechas().fechaFin(),
                bloqueo.getMotivo(), bloqueo.isActivo());
    }
}
