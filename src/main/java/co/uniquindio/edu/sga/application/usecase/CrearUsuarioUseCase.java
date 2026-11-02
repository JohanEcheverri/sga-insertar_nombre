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
 * POST /api/usuarios — Registro público (solicitante null) crea HUÉSPED; los roles internos solo los
 * asigna un ADMINISTRADOR (7.2). Contraseña segura validada y luego encriptada; correo único.
 */
public class CrearUsuarioUseCase {

    public record Command(String nombre, String apellido, String documento, String correo, String telefono,
                          String contrasenia, RolUsuario rol, LocalDate fechaNacimiento, Solicitante solicitante) { }

    private final UsuarioRepository usuarioRepository;
    private final EncriptadorContraseniaPort encriptador;

    public CrearUsuarioUseCase(UsuarioRepository usuarioRepository, EncriptadorContraseniaPort encriptador) {
        this.usuarioRepository = usuarioRepository;
        this.encriptador = encriptador;
    }

    public UsuarioDetalle ejecutar(Command cmd) {
        RolUsuario rol = cmd.rol() == null ? RolUsuario.HUESPED : cmd.rol();
        if (rol != RolUsuario.HUESPED && (cmd.solicitante() == null || !cmd.solicitante().esAdministrador())) {
            throw new AccesoDenegadoException("Solo un administrador puede asignar roles internos.");
        }
        CorreoElectronico correo = CorreoElectronico.of(cmd.correo());
        if (usuarioRepository.existePorCorreo(correo)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el correo " + correo.direccion() + ".");
        }
        PoliticaContrasenia.validar(cmd.contrasenia());
        Usuario usuario = Usuario.registrar(new NombreCompleto(cmd.nombre(), cmd.apellido()),
                new DocumentoIdentidad(cmd.documento()), correo, new Telefono(cmd.telefono()),
                encriptador.encriptar(cmd.contrasenia()), rol, new FechaNacimiento(cmd.fechaNacimiento()));
        usuarioRepository.guardar(usuario);
        return UsuarioDetalle.de(usuario);
    }
}
