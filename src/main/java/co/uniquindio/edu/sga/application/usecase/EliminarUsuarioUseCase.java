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

/**
 * DELETE /api/usuarios/{id} — Borrado lógico. Se prohíbe si es titular de reservas PENDIENTE,
 * CONFIRMADA o EN_CURSO (7.2). No elimina al titular ni sus reservas históricas.
 */
public class EliminarUsuarioUseCase {

    public record Command(UUID usuarioId) { }

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public EliminarUsuarioUseCase(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    public void ejecutar(Command cmd) {
        Usuario usuario = Cargas.usuario(usuarioRepository, cmd.usuarioId());
        if (reservaRepository.existeActivaDeUsuario(usuario.getId())) {
            throw new OperacionNoPermitidaException("No se puede eliminar un usuario titular de reservas activas.");
        }
        usuario.desactivar();
        usuarioRepository.guardar(usuario);
    }
}
