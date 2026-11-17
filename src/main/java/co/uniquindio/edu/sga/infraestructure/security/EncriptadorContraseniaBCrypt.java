package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.application.port.EncriptadorContraseniaPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Adapta BCrypt al puerto de codificación de contraseñas de la aplicación. */
@Component
public class EncriptadorContraseniaBCrypt implements EncriptadorContraseniaPort {
    private final PasswordEncoder passwordEncoder;
    public EncriptadorContraseniaBCrypt(PasswordEncoder passwordEncoder) { this.passwordEncoder = passwordEncoder; }
    @Override public String encriptar(String contraseniaPlana) { return passwordEncoder.encode(contraseniaPlana); }
}
