package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.command.*;
import co.uniquindio.edu.sga.application.exception.*;
import co.uniquindio.edu.sga.application.port.*;
import co.uniquindio.edu.sga.application.result.*;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.*;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.exception.*;
import co.uniquindio.edu.sga.domain.repository.*;
import co.uniquindio.edu.sga.domain.service.*;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/** PUT /api/usuarios/{id} — Actualiza nombre y teléfono (el correo no es editable, 7.2). Solo el propio usuario o un administrador. */
public class ModificarUsuarioUseCase {

    public record Command(UUID usuarioId, String nombre, String apellido, String telefono, Solicitante solicitante) { }

    private final UsuarioRepository usuarioRepository;

    public ModificarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDetalle ejecutar(Command cmd) {
        Solicitante s = cmd.solicitante();
        boolean propio = s.usuarioId() != null && s.usuarioId().valor().equals(cmd.usuarioId());
        if (!propio && !s.esAdministrador()) {
            throw new AccesoDenegadoException("Solo puede modificar su propio perfil.");
        }
        Usuario usuario = Cargas.usuario(usuarioRepository, cmd.usuarioId());
        usuario.actualizarNombreCompleto(new NombreCompleto(cmd.nombre(), cmd.apellido()));
        usuario.actualizarTelefono(new Telefono(cmd.telefono()));
        usuarioRepository.guardar(usuario);
        return UsuarioDetalle.de(usuario);
    }
}
