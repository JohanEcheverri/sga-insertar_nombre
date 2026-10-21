package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

//Objeto de Valor - Titular
public record Titular(String nombreCompleto, UsuarioId usuarioId, CorreoElectronico correo, Telefono telefono, DocumentoIdentidad documentoIdentidad ) {

}
