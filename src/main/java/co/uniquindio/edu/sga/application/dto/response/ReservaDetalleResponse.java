package co.uniquindio.edu.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** Vista completa de una reserva para consultas individuales y operaciones de ciclo de vida.
 * @param codigo Identificador de la reserva.
 * @param folioId Identificador del folio asociado.
 * @param apartamentoId Identificador del apartamento reservado.
 * @param nombreTitular Nombres del titular.
 * @param apellidoTitular Apellidos del titular.
 * @param usuarioId Identificador de la cuenta del titular.
 * @param correoTitular Correo de contacto del titular.
 * @param telefonoTitular Teléfono de contacto del titular.
 * @param documentoTitular Documento de identidad del titular.
 * @param ocupantes Resumen de las personas incluidas en la reserva.
 * @param fechaEntrada Fecha de llegada.
 * @param fechaSalida Fecha de salida.
 * @param identificadorExterno Referencia opcional del canal externo.
 * @param horaEstimadaLlegada Hora prevista de llegada.
 * @param canalOrigen Canal por el que se creó la reserva.
 * @param estado Estado actual de la reserva.
 * @param valorCongelado Precio conservado al confirmar la reserva.
 * @param versionPoliticaCancelacion Versión de política asociada a esta reserva.
 * @param fechaCreacion Fecha y hora de creación de la reserva.
 * @author Equipo SGA
 * @version 1.0
 */
public record ReservaDetalleResponse(
        String codigo,
        String folioId,
        String apartamentoId,
        String nombreTitular,
        String apellidoTitular,
        String usuarioId,
        String correoTitular,
        String telefonoTitular,
        String documentoTitular,
        List<String> ocupantes,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String identificadorExterno,
        LocalTime horaEstimadaLlegada,
        String canalOrigen,
        String estado,
        BigDecimal valorCongelado,
        String versionPoliticaCancelacion,
        LocalDateTime fechaCreacion
) { }
