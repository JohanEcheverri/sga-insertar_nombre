package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record BloqueoDetalle(UUID id, UUID apartamentoId, LocalDate desde, LocalDate hasta, String motivo, boolean activo) {

    public static BloqueoDetalle de(Bloqueo b) {
        return new BloqueoDetalle(b.getId().valor(), b.getApartamentoId().valor(), b.getRangoFechas().fechaInicio(),
                b.getRangoFechas().fechaFin(), b.getMotivo(), b.isActivo());
    }
}
