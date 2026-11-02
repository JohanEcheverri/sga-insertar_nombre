package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.application.result.NovedadDetalle;
import co.uniquindio.edu.sga.application.dto.response.NovedadResponse;

/** Convierte Novedad al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface NovedadMapper {
    default NovedadResponse aRespuesta(NovedadDetalle novedad) {
        return new NovedadResponse(novedad.id().toString(), novedad.apartamentoId().toString(),
                novedad.autor(), novedad.descripcion(), novedad.gravedad().name(), novedad.fechaReporte());
    }

    default NovedadResponse aRespuesta(Novedad novedad) {
        return new NovedadResponse(novedad.getId().valor().toString(),
                novedad.getApartamentoId().valor().toString(), novedad.getAutor(),
                novedad.getDescripcion(), novedad.getGravedad().name(), novedad.getFechaReporte());
    }
}
