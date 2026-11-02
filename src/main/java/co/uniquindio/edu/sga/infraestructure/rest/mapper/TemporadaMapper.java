package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.application.dto.response.TemporadaResponse;
import co.uniquindio.edu.sga.application.result.TemporadaDetalle;

/** Convierte Temporada al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface TemporadaMapper {
    default TemporadaResponse aRespuesta(TemporadaDetalle temporada) {
        return new TemporadaResponse(temporada.id().toString(), temporada.nombre(), temporada.desde(),
                temporada.hasta(), temporada.base(), temporada.tarifas().size());
    }

    default TemporadaResponse aRespuesta(Temporada temporada) {
        var rango = temporada.getRangoFechas();
        return new TemporadaResponse(temporada.getId().valor().toString(), temporada.getNombre(),
                rango == null ? null : rango.fechaInicio(), rango == null ? null : rango.fechaFin(),
                temporada.esBase(), temporada.getTarifas().size());
    }
}
