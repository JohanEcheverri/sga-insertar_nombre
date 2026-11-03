package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record TramoCancelacionEmbeddable(
        @Column(name = "dias_antelacion_minimos", nullable = false)
        int diasAntelacionMinimos,
        @Column(name = "porcentaje_retencion", nullable = false)
        double porcentajeRetencion
) { }
