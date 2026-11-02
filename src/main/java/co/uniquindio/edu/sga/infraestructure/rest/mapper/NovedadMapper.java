package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.application.dto.response.NovedadResponse;

/** Convierte Novedad al modelo público de la API. */
public interface NovedadMapper {
    static NovedadResponse aRespuesta(Novedad novedad) {
        return new NovedadResponse(novedad.getId().valor().toString(),
                novedad.getApartamentoId().valor().toString(), novedad.getAutor(),
                novedad.getDescripcion(), novedad.getGravedad().name(), novedad.getFechaReporte());
    }
}
