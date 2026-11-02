package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record ApartamentoDetalle(UUID id, String identificador, String descripcion, int capacidad, int dormitorios,
                                 EstadoOperativo estadoOperativo, boolean disponible, boolean activo) {

    public static ApartamentoDetalle de(Apartamento a) {
        return new ApartamentoDetalle(a.getId().valor(), a.getIdentificadorUnico(), a.getDescripcion(),
                a.getCapacidad().aforoMax(), a.getDormitorios().size(), a.getEstadoOperativo(),
                a.isDisponible(), a.isActivo());
    }
}
