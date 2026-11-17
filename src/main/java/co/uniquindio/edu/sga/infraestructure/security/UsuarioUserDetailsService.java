package co.uniquindio.edu.sga.infraestructure.security;

import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Carga las credenciales y roles de un usuario para Spring Security. */
@Service
public class UsuarioUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;

    public UsuarioUserDetailsService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CorreoElectronico correo;
        try {
            correo = CorreoElectronico.of(username);
        } catch (RuntimeException exception) {
            throw new UsernameNotFoundException("Credenciales inválidas.", exception);
        }
        var usuario = usuarios.buscarPorCorreo(correo).filter(u -> u.estaActivo())
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas."));
        return User.withUsername(usuario.getCorreo().direccion())
                .password(usuario.getContrasenia())
                .authorities("ROLE_" + usuario.getRol().name())
                .build();
    }
}
