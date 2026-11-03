package co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper;

import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.NovedadEntity;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** Convierte novedades entre el dominio y JPA. */
@Component
public class NovedadPersistenceMapper {
    /** Aplana la novedad, incluido su estado de anulación. */
    public NovedadEntity aEntidad(Novedad n) {
        var e = new NovedadEntity(); e.setId(n.getId().valor().toString()); e.setApartamentoId(n.getApartamentoId().valor().toString());
        e.setAutor(n.getAutor()); e.setDescripcion(n.getDescripcion()); e.setGravedad(n.getGravedad().name()); e.setFechaReporte(n.getFechaReporte()); e.setAnulada(n.isAnulada()); return e;
    }
    /** Reconstruye la novedad desde sus columnas persistidas. */
    public Novedad aDominio(NovedadEntity e) {
        var n = Novedad.crear(NovedadId.of(UUID.fromString(e.getId())), ApartamentoId.of(UUID.fromString(e.getApartamentoId())),
                e.getAutor(), e.getDescripcion(), GravedadNovedad.valueOf(e.getGravedad()), e.getFechaReporte());
        if (e.isAnulada()) n.anular(); return n;
    }
}
