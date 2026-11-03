package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public record TramoCancelacionEmbeddable(
        @Column(name = "dias_antelacion_minimos", nullable = false)
        int diasAntelacionMinimos,
        @Column(name = "porcentaje_retencion", nullable = false, precision = 5, scale = 4)
        BigDecimal porcentajeRetencion
) { }
