package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.security.SolicitantePrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

/** Adapta el principal de Spring Security al solicitante de la aplicación. */
@Component
public class SolicitanteActualSecurityAdapter implements SolicitanteActualPort {
    @Override
    public Solicitante obtener() {
        return autenticadoOpcional().orElseThrow(() -> new AccesoDenegadoException("La petición requiere un usuario autenticado."));
    }

    @Override
    public Optional<Solicitante> autenticadoOpcional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SolicitantePrincipal principal)) return Optional.empty();
        return Optional.of(principal.aSolicitante());
    }
}
