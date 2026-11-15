package co.uniquindio.edu.sga.infraestructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.LocalDate;
import java.util.List;

@ConfigurationProperties(prefix = "sga.usuarios-iniciales")
public record UsuariosInicialesProperties(boolean habilitados, List<UsuarioInicial> usuarios) {
    public UsuariosInicialesProperties {
        usuarios = usuarios == null ? List.of() : List.copyOf(usuarios);
    }

    public record UsuarioInicial(String nombre, String apellido, String documento, String correo,
                                 String telefono, String contrasena, String rol, LocalDate fechaNacimiento) { }
}
