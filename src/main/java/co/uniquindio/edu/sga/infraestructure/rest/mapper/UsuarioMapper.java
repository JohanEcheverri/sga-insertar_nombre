package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Usuario;

/** Convierte Usuario al modelo público de la API; nunca expone la contraseña. */
public interface UsuarioMapper {
    static UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(usuario.getId().valor().toString(),
                usuario.getNombreCompleto(), usuario.getDocumento(), usuario.getCorreo(),
                usuario.getTelefono(), usuario.getRol(), usuario.estaActivo(), usuario.getFechaNacimiento());
    }

    public record UsuarioResponse(String id, Object nombreCompleto, Object documento,
                                  Object correo, Object telefono, Object rol,
                                  boolean activo, Object fechaNacimiento) { }
}
