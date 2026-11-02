package co.uniquindio.edu.sga.infraestructure.rest.security;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.security.SolicitantePrincipal;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.security.Principal;
import java.util.UUID;

/** Adapta el principal validado por el contenedor web a la identidad de aplicación. */
@Component
@RequestScope
public class SolicitanteActualAdapter implements SolicitanteActualPort {
    private final HttpServletRequest request;

    public SolicitanteActualAdapter(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Solicitante obtener() {
        Principal principal = request.getUserPrincipal();
        if (principal instanceof SolicitantePrincipal identidad) {
            return identidad.aSolicitante();
        }
        if (principal == null) {
            throw new AccesoDenegadoException("La petición requiere un usuario autenticado.");
        }

        Object idAtributo = request.getAttribute("usuarioId");
        Object nombreAtributo = request.getAttribute("usuarioNombre");
        Object rolAtributo = request.getAttribute("usuarioRol");
        try {
            UUID id = UUID.fromString(idAtributo == null ? principal.getName() : idAtributo.toString());
            String nombre = nombreAtributo == null ? principal.getName() : nombreAtributo.toString();
            RolUsuario rol = rolAtributo == null ? rolDesdePeticion() : RolUsuario.valueOf(rolAtributo.toString());
            return new Solicitante(UsuarioId.of(id), nombre, rol);
        } catch (IllegalArgumentException ex) {
            throw new AccesoDenegadoException("El contexto autenticado no contiene un usuario y rol válidos.");
        }
    }

    @Override
    public java.util.Optional<Solicitante> autenticadoOpcional() {
        return request.getUserPrincipal() == null ? java.util.Optional.empty() : java.util.Optional.of(obtener());
    }

    private RolUsuario rolDesdePeticion() {
        for (RolUsuario rol : RolUsuario.values()) {
            if (request.isUserInRole(rol.name())) return rol;
        }
        throw new AccesoDenegadoException("El usuario autenticado no tiene un rol reconocido.");
    }
}
