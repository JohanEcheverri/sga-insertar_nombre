package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record ApartamentoDetalle(UUID id, String identificador, String descripcion, int capacidad, List<String> dormitorios,
                                 EstadoOperativo estadoOperativo, boolean disponible, boolean activo) {

    public static ApartamentoDetalle de(Apartamento a) {
        return new ApartamentoDetalle(a.getId().valor(), a.getIdentificadorUnico(), a.getDescripcion(),
                a.getCapacidad().aforoMax(), a.getDormitorios().stream()
                        .map(d -> d.nombre() + ": " + d.cantidadCamas() + " cama(s) " + d.tipoCamas()).toList(), a.getEstadoOperativo(),
                a.isDisponible(), a.isActivo());
    }
}
