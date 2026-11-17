package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.result.UsuarioDetalle;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Caso de uso que consulta la lista de usuarios del sistema. */
@Service
public class ListarUsuariosUseCase {
    private final UsuarioRepository usuarios;
    public ListarUsuariosUseCase(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    public List<UsuarioDetalle> ejecutar(Solicitante solicitante) {
        if (solicitante == null || !solicitante.esAdministrador()) throw new AccesoDenegadoException("Solo administración puede listar usuarios.");
        return usuarios.buscarTodos().stream().map(UsuarioDetalle::de).toList();
    }
}
