package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Datos de perfil para actualizar un usuario existente.
 * @param nombre Nombres actualizados del usuario.
 * @param documento Número de documento actualizado.
 * @param correo Correo electrónico actualizado.
 * @param telefono Teléfono actualizado.
 * @param contrasena Nueva contraseña opcional; si se informa debe cumplir la longitud mínima.
 * @param rol Rol actualizado del usuario.
 * @param fechaNacimiento Fecha de nacimiento del usuario.
 * @param activo Estado de activación de la cuenta.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El documento de identidad es obligatorio")
        String documento,

        @NotBlank(message = "El correo electrónico es obligatorio") @Email(message = "El correo electrónico no tiene un formato válido")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,

        @NotBlank(message = "El rol es obligatorio")
        String rol,

        @NotNull(message = "La fecha de nacimiento es obligatoria") @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento,

        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) { }
