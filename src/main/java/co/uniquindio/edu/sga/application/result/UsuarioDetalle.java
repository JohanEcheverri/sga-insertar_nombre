package co.uniquindio.edu.sga.application.result;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import java.math.BigDecimal; import java.time.*; import java.util.*;

/** Resultado de aplicación: datos planos, nunca entidades de dominio. */
/** Nunca expone la contraseña (ni su hash). */
public record UsuarioDetalle(UUID id, String nombre, String correo, String telefono, String documento,
                             RolUsuario rol, boolean activo, LocalDate fechaNacimiento) {

    public static UsuarioDetalle de(Usuario u) {
        return new UsuarioDetalle(u.getId().valor(), u.getNombreCompleto().obtenerNombreCompleto(),
                u.getCorreo().direccion(), u.getTelefono().numeroTelefono(), u.getDocumento().numero(),
                u.getRol(), u.estaActivo(), u.getFechaNacimiento().valor());
    }
}
