package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
public record ReservaDetalle(UUID id, UUID folioId, UUID apartamentoId, EstadoReserva estado, CanalOrigen canal,
                             LocalDate entrada, LocalDate salida, LocalTime horaEstimadaLlegada,
                             String titular, String documentoTitular, int ocupantes, BigDecimal valor,
                             String versionPolitica, LocalDateTime fechaCreacion, UUID usuarioId,
                             String nombreTitular, String apellidoTitular, String correoTitular,
                             String telefonoTitular, List<String> detalleOcupantes, String identificadorExterno) {

    public static ReservaDetalle de(Reserva r) {
        return new ReservaDetalle(r.getId().valor(), r.getFolioId().valor(), r.getApartamentoId().valor(),
                r.getEstado(), r.getCanalOrigen(), r.getEstancia().fechaEntrada(), r.getEstancia().fechaSalida(),
                r.getHoraEstimadaLlegada(), r.getTitular().nombreCompleto().obtenerNombreCompleto(),
                r.getTitular().documentoIdentidad().numero(), r.getOcupantes().size(),
                r.getValorCongelado().valor(), r.getPoliticaCancelacion().version().valor(), r.getFechaCreacion(),
                r.getTitular().usuarioId() == null ? null : r.getTitular().usuarioId().valor(),
                r.getTitular().nombreCompleto().nombre(), r.getTitular().nombreCompleto().apellido(),
                r.getTitular().correo().direccion(), r.getTitular().telefono().numeroTelefono(),
                r.getOcupantes().stream().map(o -> o.nombreCompleto().obtenerNombreCompleto()
                        + " (" + o.documentoIdentidad().numero() + ")").toList(), r.getIdentificadorExterno());
    }
}
