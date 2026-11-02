package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Información requerida para registrar un usuario.
 * @param nombre Nombres del usuario.
 * @param apellido Apellidos del usuario.
 * @param documento Número de documento de identidad.
 * @param correo Dirección de correo electrónico de contacto.
 * @param telefono Número telefónico de contacto.
 * @param contrasena Credencial inicial del usuario.
 * @param rol Rol asignado dentro del sistema.
 * @param fechaNacimiento Fecha de nacimiento del usuario.
 * @author Equipo SGA
 * @version 1.0
 */
public record CrearUsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El documento de identidad es obligatorio")
        String documento,

        @NotBlank(message = "El correo electrónico es obligatorio") @Email(message = "El correo electrónico no tiene un formato válido")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono,

        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String contrasena,

        @NotBlank(message = "El rol es obligatorio")
        String rol,

        @NotNull(message = "La fecha de nacimiento es obligatoria") @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento
) { }
