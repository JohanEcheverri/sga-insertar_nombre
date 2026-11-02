package co.uniquindio.edu.sga.application.security;

/** Puerto para obtener la identidad validada que inició la petición actual. */
public interface SolicitanteActualPort {
    Solicitante obtener();

    java.util.Optional<Solicitante> autenticadoOpcional();
}
