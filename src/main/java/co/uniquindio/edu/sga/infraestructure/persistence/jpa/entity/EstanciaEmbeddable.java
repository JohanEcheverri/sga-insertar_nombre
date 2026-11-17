package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

/** Modelo JPA embebible que almacena los datos de estancia. */
@Embeddable
public record EstanciaEmbeddable(
        @Column(name = "fecha_entrada", nullable = false)
        LocalDate fechaEntrada,

        @Column(name = "fecha_salida", nullable = false)
        LocalDate fechaSalida
) {
}