package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import java.time.LocalDate;

/** Modelo JPA embebible que almacena los datos de ocupante. */
@Embeddable
public record OcupanteEmbeddable(
        @Column(name = "Documento", nullable = false, length = 20)
        String documento,

        @Column(name = "Nombre", nullable = false, length = 100)
        String nombre,

        @Column(name = "Apellido", nullable = false, length = 100)
        String apellido,

        @Column(name = "fecha_nacimiento", nullable = false)
        LocalDate fechaNacimiento
) {
}