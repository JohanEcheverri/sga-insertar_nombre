package co.uniquindio.edu.sga.application.command;

import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.DocumentoIdentidad;
import co.uniquindio.edu.sga.domain.valueobject.NombreCompleto;
import co.uniquindio.edu.sga.domain.valueobject.Telefono;
import co.uniquindio.edu.sga.domain.valueobject.Titular;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

import java.util.UUID;

/** usuarioId es opcional: un titular puede no tener cuenta (3.6). */
public record TitularDatos(String nombre, String apellido, UUID usuarioId, String correo,
                           String telefono, String documento) {

    public Titular aDominio() {
        return new Titular(new NombreCompleto(nombre, apellido),
                usuarioId == null ? null : UsuarioId.of(usuarioId),
                CorreoElectronico.of(correo), new Telefono(telefono), new DocumentoIdentidad(documento));
    }
}
