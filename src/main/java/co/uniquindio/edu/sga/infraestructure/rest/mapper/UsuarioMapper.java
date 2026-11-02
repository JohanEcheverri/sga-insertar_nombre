package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.application.dto.response.UsuarioResponse;
import co.uniquindio.edu.sga.application.result.UsuarioDetalle;

/** Convierte Usuario al modelo público de la API; nunca expone la contraseña. */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    default UsuarioResponse aRespuesta(UsuarioDetalle usuario) {
        return new UsuarioResponse(usuario.id().toString(), usuario.nombre(), usuario.apellido(), usuario.documento(),
                usuario.correo(), usuario.telefono(), usuario.rol().name(), usuario.activo(), usuario.fechaNacimiento());
    }

    default UsuarioResponse aRespuesta(Usuario usuario) {
        return new UsuarioResponse(usuario.getId().valor().toString(),
                usuario.getNombreCompleto().nombre(), usuario.getNombreCompleto().apellido(),
                usuario.getDocumento().numero(), usuario.getCorreo().direccion(),
                usuario.getTelefono().numeroTelefono(), usuario.getRol().name(), usuario.estaActivo(),
                usuario.getFechaNacimiento().valor());
    }
}
