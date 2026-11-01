package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> obtenerPorId(UsuarioId id);

    Optional<Usuario> obtenerPorCorreo(CorreoElectronico correo);

    List<Usuario> listarTodos();
}