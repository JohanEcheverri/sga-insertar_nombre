package co.uniquindio.edu.sga.domain.valueobject;

import java.util.UUID;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record AlojamientoId(UUID valor) {

    // Constructor compacto
    public AlojamientoId {
        if (valor == null) {
            throw new ReglaDominioException("El valor del alojamiento no puede ser nulo.");
        }
    }

    // Factoría desde UUID
    public static AlojamientoId of(UUID valor) {
        return new AlojamientoId(valor);
    }

    // Factoría desde String (Mappers y REST)
    public static AlojamientoId of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El texto del ID del alojamiento no puede estar vacío.");
        }
        try {
            return new AlojamientoId(UUID.fromString(valor));
        } catch (IllegalArgumentException e) {
            throw new ReglaDominioException("El formato del UUID de alojamiento es inválido: " + valor);
        }
    }

    // Generador aleatorio
    public static AlojamientoId generar() {
        return new AlojamientoId(UUID.randomUUID());
    }

    // Representación limpia en texto
    @Override
    public String toString() {
        return valor.toString();
    }
}


