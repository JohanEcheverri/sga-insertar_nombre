package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Novedad;

/** Convierte Novedad al modelo público de la API. */
public interface NovedadMapper {
    static NovedadResponse aRespuesta(Novedad novedad) {
        return new NovedadResponse(novedad.getId().valor().toString(),
                novedad.getApartamentoId().valor().toString(), novedad.getAutor(),
                novedad.getDescripcion(), novedad.getGravedad(), novedad.getFechaReporte());
    }

    public record NovedadResponse(String id, String apartamentoId, String autor,
                                  String descripcion, Object gravedad, Object fechaReporte) { }
}
