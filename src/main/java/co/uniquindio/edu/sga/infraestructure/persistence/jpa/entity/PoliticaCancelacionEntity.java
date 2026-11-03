package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Política versionada con sus tramos y porcentaje de retención por no-show. */
@Entity @Table(name="politicas_cancelacion") @Getter @Setter @NoArgsConstructor
public class PoliticaCancelacionEntity {
    @Id private String version;
    @Column(nullable=false) private LocalDate vigenteDesde;
    @Column(precision=5, scale=4, nullable=false) private BigDecimal porcentajeNoShow;
    @ElementCollection @CollectionTable(name="politica_tramos", joinColumns=@JoinColumn(name="version"))
    private List<TramoCancelacionEmbeddable> tramos = new ArrayList<>();
}
