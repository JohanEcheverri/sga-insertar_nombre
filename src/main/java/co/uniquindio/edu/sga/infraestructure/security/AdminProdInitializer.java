package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.application.support.PoliticaContrasenia;
import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class AdminProdInitializer implements ApplicationRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final String nombre;
    private final String correo;
    private final String clave;

    public AdminProdInitializer(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
            @Value("${sga.admin.nombre:Administrador}") String nombre,
            @Value("${sga.admin.correo:}") String correo,
            @Value("${sga.admin.clave:}") String clave) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
    }

    @Override
    public void run(ApplicationArguments args) {
        if ((correo == null || correo.isBlank()) && (clave == null || clave.isBlank())) return;
        if (correo == null || correo.isBlank() || clave == null || clave.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalStateException("SGA_ADMIN_NOMBRE, SGA_ADMIN_CORREO y SGA_ADMIN_CLAVE deben configurarse juntos.");
        }
        CorreoElectronico correoElectronico = CorreoElectronico.of(correo);
        if (usuarios.existePorCorreo(correoElectronico)) return;
        PoliticaContrasenia.validar(clave);
        usuarios.guardar(Usuario.registrar(new NombreCompleto(nombre, "SGA"),
                new DocumentoIdentidad("SGA-ADMIN"), correoElectronico, new Telefono("3000000000"),
                passwordEncoder.encode(clave), RolUsuario.ADMINISTRADOR, new FechaNacimiento(java.time.LocalDate.of(1980, 1, 1))));
    }
}
