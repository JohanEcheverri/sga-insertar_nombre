package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;

/** Convierte Bloqueo al modelo público de la API. */
public interface BloqueoMapper {
    static BloqueoResponse aRespuesta(Bloqueo bloqueo) {
        return new BloqueoResponse(bloqueo.getId().valor().toString(),
                bloqueo.getApartamentoId().valor().toString(), bloqueo.getRangoFechas(),
                bloqueo.getMotivo(), bloqueo.isActivo());
    }

    public record BloqueoResponse(String id, String apartamentoId, Object rangoFechas,
                                  String motivo, boolean activo) { }
}
