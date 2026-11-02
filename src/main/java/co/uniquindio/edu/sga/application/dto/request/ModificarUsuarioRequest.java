package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Datos de perfil para actualizar un usuario existente.
 * @param nombre Nombres actualizados del usuario.
 * @param apellido Apellidos actualizados del usuario.
 * @param telefono Teléfono actualizado.
 * @author Equipo SGA
 * @version 1.0
 */
public record ModificarUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotBlank(message = "El teléfono es obligatorio")
        String telefono
) { }
