package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record PoliticaEmbeddable(
        @Column(name = "version", nullable = false, length = 50)
        String version,

        @Column(name = "fecha_vigencia", nullable = false)
        LocalDate vigenteDesde
) {
}
