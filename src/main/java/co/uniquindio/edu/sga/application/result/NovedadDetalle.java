package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record NovedadDetalle(UUID id, UUID apartamentoId, String autor, String descripcion,
                             GravedadNovedad gravedad, LocalDateTime fechaReporte, boolean anulada) {

    public static NovedadDetalle de(Novedad n) {
        return new NovedadDetalle(n.getId().valor(), n.getApartamentoId().valor(), n.getAutor(),
                n.getDescripcion(), n.getGravedad(), n.getFechaReporte(), n.isAnulada());
    }
}
