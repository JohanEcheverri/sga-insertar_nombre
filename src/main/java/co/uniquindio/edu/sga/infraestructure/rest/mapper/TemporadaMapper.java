package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.application.dto.response.TemporadaResponse;

/** Convierte Temporada al modelo público de la API. */
public interface TemporadaMapper {
    static TemporadaResponse aRespuesta(Temporada temporada) {
        var rango = temporada.getRangoFechas();
        return new TemporadaResponse(temporada.getId().valor().toString(), temporada.getNombre(),
                rango == null ? null : rango.fechaInicio(), rango == null ? null : rango.fechaFin(),
                temporada.esBase(), temporada.getTarifas().size());
    }
}
