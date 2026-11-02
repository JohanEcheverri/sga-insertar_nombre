package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UsuarioRepositoryInMemory implements UsuarioRepository {

    private final Map<UsuarioId, Usuario> usuarios = new LinkedHashMap<>();

    @Override
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(UsuarioId id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(CorreoElectronico correo) {
        return usuarios.values().stream().filter(u -> u.getCorreo().equals(correo)).findFirst();
    }

    @Override
    public boolean existePorCorreo(CorreoElectronico correo) {
        return buscarPorCorreo(correo).isPresent();
    }
}
