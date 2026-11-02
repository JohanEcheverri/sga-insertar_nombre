package co.uniquindio.edu.sga.application.security;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

/**
 * Quién ejecuta el caso de uso (la infraestructura lo arma desde el JWT). La protección por ROL de
 * cada endpoint es de infraestructura; la verificación sobre el PROPIETARIO del recurso (7.2) es de
 * aplicación y vive aquí.
 */
public record Solicitante(UsuarioId usuarioId, String nombre, RolUsuario rol) {

    public boolean esAdministrador() {
        return rol == RolUsuario.ADMINISTRADOR;
    }

    public boolean esPersonalInterno() {
        return rol == RolUsuario.ADMINISTRADOR || rol == RolUsuario.RECEPCIONISTA;
    }

    /** Un huésped solo accede a reservas/folios de las que es titular. */
    public void exigirAccesoA(Reserva reserva) {
        if (esPersonalInterno()) return;
        boolean propietario = rol == RolUsuario.HUESPED && usuarioId != null
                && reserva.getTitular().usuarioId() != null
                && reserva.getTitular().usuarioId().equals(usuarioId);
        if (!propietario) {
            throw new AccesoDenegadoException("No tiene permiso para acceder a esta reserva.");
        }
    }

    public void exigirAdministrador() {
        if (!esAdministrador()) {
            throw new AccesoDenegadoException("Esta operación es una decisión administrativa.");
        }
    }
}
