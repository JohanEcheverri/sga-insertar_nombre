package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Representa en una fila el precio de una temporada para un apartamento. */
@Embeddable @Getter @Setter @NoArgsConstructor
public class TarifaEmbeddable {
    @Column(name="apartamento_id", nullable=false) private String apartamentoId;
    @Column(name="precio_por_noche", precision=15, scale=2, nullable=false) private BigDecimal precioPorNoche;
}
