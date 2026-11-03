package co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper;

import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** Convierte usuarios entre el modelo de dominio y su representación JPA. */
@Component
public class UsuarioPersistenceMapper {
    /** Aplana los valores del usuario en una entidad persistible. */
    public UsuarioEntity aEntidad(Usuario u) {
        var e = new UsuarioEntity(); e.setId(u.getId().valor().toString());
        e.setNombre(u.getNombreCompleto().nombre()); e.setApellido(u.getNombreCompleto().apellido());
        e.setDocumento(u.getDocumento().numero()); e.setCorreo(u.getCorreo().direccion()); e.setTelefono(u.getTelefono().numeroTelefono());
        e.setContrasenia(u.getContrasenia()); e.setRol(u.getRol().name()); e.setActivo(u.estaActivo()); e.setFechaNacimiento(u.getFechaNacimiento().valor()); return e;
    }
    /** Reconstruye el usuario y sus objetos de valor. */
    public Usuario aDominio(UsuarioEntity e) {
        return Usuario.crear(new NombreCompleto(e.getNombre(), e.getApellido()), UsuarioId.of(UUID.fromString(e.getId())),
                new DocumentoIdentidad(e.getDocumento()), CorreoElectronico.of(e.getCorreo()), new Telefono(e.getTelefono()),
                e.getContrasenia(), RolUsuario.valueOf(e.getRol()), e.isActivo(), new FechaNacimiento(e.getFechaNacimiento()));
    }
}
