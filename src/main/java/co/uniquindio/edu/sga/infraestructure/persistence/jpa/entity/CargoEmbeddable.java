package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Cargo contabilizado en un folio. */
@Embeddable @Getter @Setter @NoArgsConstructor
public class CargoEmbeddable {
    private String tipo;
    private String concepto;
    @Column(precision=15, scale=2) private BigDecimal monto;
    private LocalDateTime fechaRegistro;
}
