package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.application.port.EncriptadorContraseniaPort;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/** Adaptador local para proteger contraseñas sin requerir servicios externos. */
@Component
public class EncriptadorContraseniaPbkdf2 implements EncriptadorContraseniaPort {
    private static final int ITERACIONES = 210_000;
    private static final int BITS = 256;
    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    public String encriptar(String contraseniaPlana) {
        byte[] sal = new byte[16];
        aleatorio.nextBytes(sal);
        PBEKeySpec especificacion = new PBEKeySpec(contraseniaPlana.toCharArray(), sal, ITERACIONES, BITS);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).getEncoded();
            return "pbkdf2-sha256$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No fue posible proteger la contraseña.", ex);
        } finally {
            especificacion.clearPassword();
        }
    }
}
