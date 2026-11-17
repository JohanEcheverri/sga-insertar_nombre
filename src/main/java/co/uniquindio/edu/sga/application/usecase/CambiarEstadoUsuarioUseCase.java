package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.exception.OperacionNoPermitidaException;
import co.uniquindio.edu.sga.application.result.UsuarioDetalle;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.Cargas;
import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso que cambia el estado de un usuario. */
@Service
@Transactional
public class CambiarEstadoUsuarioUseCase {
    public record Command(UUID usuarioId, boolean activo, Solicitante solicitante) { }
    private final UsuarioRepository usuarios;
    private final ReservaRepository reservas;
    public CambiarEstadoUsuarioUseCase(UsuarioRepository usuarios, ReservaRepository reservas) { this.usuarios = usuarios; this.reservas = reservas; }
    public UsuarioDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null || !cmd.solicitante().esAdministrador()) throw new AccesoDenegadoException("Solo administración puede activar o desactivar usuarios.");
        Usuario usuario = Cargas.usuario(usuarios, cmd.usuarioId());
        if (!cmd.activo() && usuario.estaActivo() && reservas.existeActivaDeUsuario(usuario.getId())) {
            throw new OperacionNoPermitidaException("No se puede desactivar un usuario titular de reservas activas.");
        }
        if (cmd.activo() && !usuario.estaActivo()) usuario.activar();
        if (!cmd.activo() && usuario.estaActivo()) usuario.desactivar();
        usuarios.guardar(usuario);
        return UsuarioDetalle.de(usuario);
    }
}
