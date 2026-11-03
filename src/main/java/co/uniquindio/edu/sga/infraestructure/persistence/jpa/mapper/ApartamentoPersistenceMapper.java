package co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** Convierte apartamentos entre el modelo de dominio y su representación JPA. */
@Component
public class ApartamentoPersistenceMapper {
    /** Construye la entidad JPA con todos los datos del apartamento. */
    public ApartamentoEntity aEntidad(Apartamento a) {
        var e = new ApartamentoEntity();
        e.setId(a.getId().valor().toString()); e.setIdentificadorUnico(a.getIdentificadorUnico());
        e.setDescripcion(a.getDescripcion()); e.setCapacidad(a.getCapacidad().aforoMax());
        e.setEstadoOperativo(a.getEstadoOperativo().name()); e.setDisponible(a.isDisponible()); e.setActivo(a.isActivo());
        a.getDormitorios().forEach(d -> { var x = new DormitorioEmbeddable(); x.setNombre(d.nombre()); x.setCantidadCamas(d.cantidadCamas()); x.setTipoCamas(d.tipoCamas()); e.getDormitorios().add(x); });
        return e;
    }
    /** Reconstruye el agregado desde una entidad JPA. */
    public Apartamento aDominio(ApartamentoEntity e) {
        var dormitorios = e.getDormitorios().stream().map(d -> new Dormitorio(d.getNombre(), d.getCantidadCamas(), d.getTipoCamas())).toList();
        var estado = EstadoOperativo.valueOf(e.getEstadoOperativo());
        var a = Apartamento.crear(new Capacidad((byte)e.getCapacidad()), e.getDescripcion(), dormitorios, estado,
                ApartamentoId.of(UUID.fromString(e.getId())), e.getIdentificadorUnico(), e.isDisponible());
        if (!e.isActivo()) a.retirar();
        return a;
    }
}
