package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.application.support.PoliticaContrasenia;
import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sga.usuarios-iniciales.habilitados", havingValue = "true", matchIfMissing = true)
public class DefaultUserInitializer implements ApplicationRunner {
    private final UsuariosInicialesProperties properties;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UsuariosInicialesProperties properties, UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (var configurado : properties.usuarios()) {
            validar(configurado);
            CorreoElectronico correo = CorreoElectronico.of(configurado.correo());
            if (usuarios.existePorCorreo(correo)) continue;
            RolUsuario rol = RolUsuario.valueOf(configurado.rol());
            PoliticaContrasenia.validar(configurado.contrasena());
            usuarios.guardar(Usuario.registrar(new NombreCompleto(configurado.nombre(), configurado.apellido()),
                    new DocumentoIdentidad(configurado.documento()), correo, new Telefono(configurado.telefono()),
                    passwordEncoder.encode(configurado.contrasena()), rol, new FechaNacimiento(configurado.fechaNacimiento())));
        }
    }

    private static void validar(UsuariosInicialesProperties.UsuarioInicial usuario) {
        if (usuario == null || vacio(usuario.nombre()) || vacio(usuario.apellido()) || vacio(usuario.documento())
                || vacio(usuario.correo()) || vacio(usuario.telefono()) || vacio(usuario.contrasena())
                || vacio(usuario.rol()) || usuario.fechaNacimiento() == null) {
            throw new IllegalStateException("Cada usuario inicial debe tener nombre, apellido, documento, correo, teléfono, contraseña, rol y fecha de nacimiento.");
        }
        try { RolUsuario.valueOf(usuario.rol()); }
        catch (IllegalArgumentException ex) { throw new IllegalStateException("El rol de un usuario inicial no es válido.", ex); }
    }

    private static boolean vacio(String valor) { return valor == null || valor.isBlank(); }
}
