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

/** GET /api/usuarios/{id} — Información y rol de un usuario; el propio usuario o personal interno. */
public class BuscarUsuarioPorCodigoUseCase {

    public record Command(UUID usuarioId, Solicitante solicitante) { }

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioPorCodigoUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioDetalle ejecutar(Command cmd) {
        Solicitante s = cmd.solicitante();
        boolean propio = s.usuarioId() != null && s.usuarioId().valor().equals(cmd.usuarioId());
        if (!propio && !s.esPersonalInterno()) {
            throw new AccesoDenegadoException("No tiene permiso para consultar este usuario.");
        }
        return UsuarioDetalle.de(Cargas.usuario(usuarioRepository, cmd.usuarioId()));
    }
}
