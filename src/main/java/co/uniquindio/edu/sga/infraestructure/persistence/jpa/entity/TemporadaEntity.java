package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Temporada")
@Getter
@Setter
@NoArgsConstructor
public class TemporadaEntity {

    @Id
    private String id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "multiplicador_tarifa", nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplicadorTarifa;
    @Column(name = "es_base", nullable = false)
    private boolean esBase;
    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "temporada_tarifas", joinColumns = @jakarta.persistence.JoinColumn(name = "temporada_id"))
    private java.util.List<TarifaEmbeddable> tarifas = new java.util.ArrayList<>();
}
