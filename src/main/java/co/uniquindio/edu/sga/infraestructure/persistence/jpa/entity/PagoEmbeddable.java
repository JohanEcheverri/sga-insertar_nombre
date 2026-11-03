package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pago aplicado a un folio. */
@Embeddable @Getter @Setter @NoArgsConstructor
public class PagoEmbeddable {
    @Column(precision=15, scale=2) private BigDecimal monto;
    private String medio;
    private String referencia;
    private LocalDateTime fechaRegistro;
}
