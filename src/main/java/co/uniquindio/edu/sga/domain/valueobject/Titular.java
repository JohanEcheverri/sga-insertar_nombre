package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

/**
 * Objeto de Valor - Titular.
 * 3.6: un titular puede o no tener usuario asociado (reserva creada por recepción o canal externo),
 * por eso usuarioId es OPCIONAL (puede ser null).
 */
public record Titular(NombreCompleto nombreCompleto, UsuarioId usuarioId, CorreoElectronico correo,
                      Telefono telefono, DocumentoIdentidad documentoIdentidad) {
    public Titular {
        if (nombreCompleto == null) {
            throw new ReglaDominioException("El titular debe tener nombre completo.");
        }
        if (correo == null || telefono == null || documentoIdentidad == null) {
            throw new ReglaDominioException("Los datos de identificación y contacto del titular son obligatorios.");
        }
    }

    public boolean tieneUsuario() {
        return usuarioId != null;
    }
}
