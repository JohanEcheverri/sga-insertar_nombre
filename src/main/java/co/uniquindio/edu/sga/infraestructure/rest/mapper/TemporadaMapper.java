package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Temporada;

/** Convierte Temporada al modelo público de la API. */
public interface TemporadaMapper {
    static TemporadaResponse aRespuesta(Temporada temporada) {
        return new TemporadaResponse(temporada.getId().valor().toString(), temporada.getNombre(),
                temporada.getRangoFechas(), temporada.esBase(), temporada.getTarifas());
    }

    public record TemporadaResponse(String id, String nombre, Object rangoFechas,
                                    boolean base, Object tarifas) { }
}
