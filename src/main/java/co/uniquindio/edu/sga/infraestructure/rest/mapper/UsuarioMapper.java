package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.application.dto.response.UsuarioResponse;

/** Convierte Usuario al modelo público de la API; nunca expone la contraseña. */
public interface UsuarioMapper {
    static UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(usuario.getId().valor().toString(),
                usuario.getNombreCompleto().nombre(), usuario.getNombreCompleto().apellido(),
                usuario.getDocumento().numero(), usuario.getCorreo().direccion(),
                usuario.getTelefono().numeroTelefono(), usuario.getRol().name(), usuario.estaActivo(),
                usuario.getFechaNacimiento().valor());
    }
}
