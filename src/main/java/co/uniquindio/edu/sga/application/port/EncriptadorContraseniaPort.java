package co.uniquindio.edu.sga.application.port;

/** El dominio solo recibe contraseñas ya protegidas; el hashing (p. ej. BCrypt) vive en infraestructura. */
public interface EncriptadorContraseniaPort {

    String encriptar(String contraseniaPlana);
}
