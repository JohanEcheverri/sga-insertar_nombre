package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

/** Modelo JPA embebible que almacena los datos de politica. */
@Embeddable
public record PoliticaEmbeddable(
        @Column(name = "version", nullable = false, length = 50)
        String version,

        @Column(name = "fecha_vigencia", nullable = false)
        LocalDate vigenteDesde
) {
}
