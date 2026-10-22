package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

//Objeto de Valor - Titular
public record Titular(String nombreCompleto, UsuarioId usuarioId, CorreoElectronico correo, Telefono telefono, DocumentoIdentidad documentoIdentidad) {
    public Titular {
        // Regla del titular: el nombre completo y los datos de contacto e identificación son obligatorios.
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new ReglaDominioException("El titular debe tener nombre completo.");
        }
        if (usuarioId == null || correo == null || telefono == null || documentoIdentidad == null) {
            throw new ReglaDominioException("Los datos de identificación y contacto del titular son obligatorios.");
        }
        nombreCompleto = nombreCompleto.trim();
    }
}
