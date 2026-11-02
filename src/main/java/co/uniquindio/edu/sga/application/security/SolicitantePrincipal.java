package co.uniquindio.edu.sga.application.security;

import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;

import java.security.Principal;
import java.util.UUID;

/** Identidad autenticada que la infraestructura de autenticación coloca como principal. */
public record SolicitantePrincipal(UUID id, String nombre, RolUsuario rol) implements Principal {
    @Override
    public String getName() {
        return id.toString();
    }

    public Solicitante aSolicitante() {
        return new Solicitante(co.uniquindio.edu.sga.domain.valueobject.UsuarioId.of(id), nombre, rol);
    }
}
