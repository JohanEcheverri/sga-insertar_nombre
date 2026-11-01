package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import co.uniquindio.edu.sga.domain.entity.Apartamento;

/** Convierte Apartamento al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface ApartamentoMapper {
    static ApartamentoResponse aRespuesta(Apartamento apartamento) {
        return new ApartamentoResponse(apartamento.getId().valor().toString(),
                apartamento.getIdentificadorUnico(), apartamento.getDescripcion(),
                apartamento.getCapacidad(), apartamento.getEstadoOperativo(),
                apartamento.getDormitorios(), apartamento.isDisponible());
    }

    public record ApartamentoResponse(String id, String identificadorUnico, String descripcion,
                                      Object capacidad, Object estadoOperativo,
                                      Object dormitorios, boolean disponible) { }
}
