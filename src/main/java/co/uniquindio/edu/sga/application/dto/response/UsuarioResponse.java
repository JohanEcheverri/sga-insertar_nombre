package co.uniquindio.edu.sga.application.dto.response;

import java.time.LocalDate;

/** Vista pública de un usuario; no expone credenciales ni contraseña.
 * @param id Identificador del usuario.
 * @param nombre Nombres del usuario.
 * @param apellido Apellidos del usuario.
 * @param documento Número del documento de identidad.
 * @param correo Correo electrónico de contacto.
 * @param telefono Teléfono de contacto.
 * @param rol Rol asignado en el sistema.
 * @param activo Indica si la cuenta está activa.
 * @param fechaNacimiento Fecha de nacimiento.
 * @author Equipo SGA
 * @version 1.0
 */
public record UsuarioResponse(
        String id,
        String nombre,
        String apellido,
        String documento,
        String correo,
        String telefono,
        String rol,
        boolean activo,
        LocalDate fechaNacimiento) { }
