package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository {

    void guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(UsuarioId id);

    Optional<Usuario> buscarPorCorreo(CorreoElectronico correo);

    List<Usuario> buscarTodos();

    /** 7.2 - El correo es único. */
    boolean existePorCorreo(CorreoElectronico correo);
}
