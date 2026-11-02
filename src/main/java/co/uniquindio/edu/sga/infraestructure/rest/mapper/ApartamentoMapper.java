package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.application.dto.response.ApartamentoResponse;

import java.util.List;

/** Convierte Apartamento al modelo público de la API. */
public interface ApartamentoMapper {
    static ApartamentoResponse aRespuesta(Apartamento a) {
        List<String> dormitorios = a.getDormitorios().stream()
                .map(d -> d.nombre() + ": " + d.cantidadCamas() + " cama(s) " + d.tipoCamas())
                .toList();
        return new ApartamentoResponse(a.getId().valor().toString(), a.getIdentificadorUnico(),
                a.getDescripcion(), (int) a.getCapacidad().aforoMax(),
                a.getEstadoOperativo().name(), dormitorios, a.isDisponible());
    }
}
