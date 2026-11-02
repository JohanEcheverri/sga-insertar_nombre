package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** Datos para solicitar una reserva y registrar al titular y sus ocupantes.
 * @param apartamentoId Identificador del apartamento solicitado.
 * @param nombreTitular Nombres del titular de la reserva.
 * @param apellidoTitular Apellidos del titular de la reserva.
 * @param usuarioId Identificador del usuario titular.
 * @param correoTitular Correo de contacto del titular.
 * @param telefonoTitular Teléfono de contacto del titular.
 * @param documentoTitular Documento de identidad del titular.
 * @param ocupantes Personas incluidas en la reserva.
 * @param fechaEntrada Fecha prevista de llegada.
 * @param fechaSalida Fecha prevista de salida.
 * @param identificadorExterno Código de referencia opcional del canal externo.
 * @param horaEstimadaLlegada Hora prevista de llegada al alojamiento.
 * @param canalOrigen Canal por el que se originó la reserva.
 * @author Equipo SGA
 * @version 1.0
 */
public record CrearReservaRequest(

        @NotBlank(message = "El identificador del apartamento es obligatorio")
        String apartamentoId,

        @NotBlank(message = "El nombre del titular es obligatorio")
        String nombreTitular,

        @NotBlank(message = "El apellido del titular es obligatorio")
        String apellidoTitular,

        @NotBlank(message = "El identificador del usuario titular es obligatorio")
        String usuarioId,

        @NotBlank(message = "El correo del titular es obligatorio") @Email(message = "El correo del titular no tiene un formato válido")
        String correoTitular,

        @NotBlank(message = "El teléfono del titular es obligatorio")
        String telefonoTitular,

        @NotBlank(message = "El documento del titular es obligatorio")
        String documentoTitular,

        @NotEmpty(message = "La reserva debe tener al menos un ocupante")
        List<@Valid OcupanteRequest> ocupantes,

        @NotNull(message = "La fecha de entrada es obligatoria") @FutureOrPresent(message = "La entrada no puede ser anterior a hoy")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria") @Future(message = "La salida debe ser futura")
        LocalDate fechaSalida,

        String identificadorExterno,

        @NotNull(message = "La hora estimada de llegada es obligatoria")
        LocalTime horaEstimadaLlegada,

        @NotBlank(message = "El canal de origen es obligatorio")
        String canalOrigen
) { }
